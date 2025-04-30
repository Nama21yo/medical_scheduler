import {
  Injectable,
  NotFoundException,
  BadRequestException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Branch } from './branch.entity';
import { CreateBranchDto } from './dto/CreateBranchDto.dto';
import { BranchSignupDto } from './dto/BranchSignUpDto.dto';
import { HeadOffice } from 'src/head-office/head-office.entity';
import { UpdateUserDto } from 'src/user/dto/updateUser.dto';

@Injectable()
export class BranchService {
  constructor(
    @InjectRepository(Branch)
    private readonly branchRepository: Repository<Branch>,
    @InjectRepository(HeadOffice)
    private readonly headofficeRepository: Repository<HeadOffice>,
  ) {}

  /**
   * Create a branch (basic details added by HeadOffice)
   */
  async create(dto: CreateBranchDto): Promise<Branch> {
    const headoffice = await this.headofficeRepository.findOne({
      where: { headoffice_id: dto.headoffice_id },
    });

    if (!headoffice) {
      throw new NotFoundException(
        `Head Office with ID ${dto.headoffice_id} not found`,
      );
    }

    const branch = this.branchRepository.create({
      ...dto,
      headoffice,
    });

    return this.branchRepository.save(branch);
  }

  /**
   * Sign up as a branch (complete profile)
   */
  async signup(email: string, dto: BranchSignupDto): Promise<Branch> {
    const branch = await this.branchRepository.findOne({
      where: { contact_email: email },
    });

    if (!branch) {
      throw new NotFoundException(`Branch with email ${email} not found`);
    }

    if (branch.is_signed_up) {
      throw new BadRequestException(
        `Branch with email ${email} has already signed up`,
      );
    }

    branch.name = dto.name;
    branch.location = dto.location;
    branch.password = dto.password; // Automatically hashed via BeforeInsert
    branch.specialization = dto.specialization;
    branch.is_signed_up = true;

    return this.branchRepository.save(branch);
  }

  /**
   * Fetch all branches
   */
  async findAll(): Promise<Branch[]> {
    return this.branchRepository.find({ relations: ['headoffice'] });
  }

  async findByEmail(email: string): Promise<any> {
    const branch = await this.branchRepository.findOne({
      where: { contact_email: email },
    });

    if (!branch) {
      throw new BadRequestException('Branch with this email does not exist');
    }

    return branch;
  }

  async update(email: string, updateDto: UpdateUserDto): Promise<any> {
    const branch = await this.findByEmail(email);
    if (!branch) {
      throw new BadRequestException('Branch not found');
    }

    // Update branch-specific fields
    if (updateDto.username) branch.name = updateDto.username;
    if (updateDto.password) branch.password = updateDto.password;
    // Save the updated branch details
    return this.branchRepository.save(branch);
  }

  async delete(id: number): Promise<void> {
    const result = await this.branchRepository.delete(id);
    if (result.affected === 0) {
      throw new NotFoundException(`Branch with ID ${id} not found`);
    }
  }
}
