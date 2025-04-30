import {
  Injectable,
  NotFoundException,
  BadRequestException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Receptionist } from './receptionist.entity';
import { AddReceptionistDto } from './dto/addReceptionistDto.dto';
import { ReceptionistSignupDto } from './dto/receptionistSignup.dto';
import { Branch } from 'src/branch/branch.entity';
import { UpdateUserDto } from 'src/user/dto/updateUser.dto';

@Injectable()
export class ReceptionistService {
  constructor(
    @InjectRepository(Receptionist)
    private readonly receptionistRepository: Repository<Receptionist>,
    @InjectRepository(Branch)
    private readonly branchRepository: Repository<Branch>,
  ) {}

  async addReceptionist(dto: AddReceptionistDto): Promise<Receptionist> {
    const branch = await this.branchRepository.findOne({
      where: { branch_id: dto.branch_id },
    });

    if (!branch) {
      throw new NotFoundException(`Branch with ID ${dto.branch_id} not found`);
    }

    const receptionist = this.receptionistRepository.create({
      email: dto.email,
      branch,
    });

    return this.receptionistRepository.save(receptionist);
  }

  async findAll(): Promise<Receptionist[]> {
    return this.receptionistRepository.find({ relations: ['branch'] });
  }
  async signup(
    email: string,
    dto: ReceptionistSignupDto,
  ): Promise<Receptionist> {
    const receptionist = await this.receptionistRepository.findOne({
      where: { email },
    });

    if (!receptionist) {
      throw new NotFoundException(`Receptionist with email ${email} not found`);
    }

    if (receptionist.is_signed_up) {
      throw new BadRequestException(
        `Receptionist with email ${email} has already signed up`,
      );
    }
    receptionist.name = dto.name;
    receptionist.password = dto.password; // Automatically hashed via BeforeInsert
    receptionist.is_signed_up = true;

    return this.receptionistRepository.save(receptionist);
  }

  async findByEmail(email: string): Promise<any> {
    const receptionist = await this.receptionistRepository.findOne({
      where: { email },
    });

    if (!receptionist) {
      throw new BadRequestException(
        'Receptionist with this email does not exist',
      );
    }

    return receptionist;
  }

  async update(email: string, updateDto: UpdateUserDto): Promise<any> {
    const receptionist = await this.findByEmail(email);
    if (!receptionist) {
      throw new BadRequestException('Receptionist not found');
    }

    // Update receptionist-specific fields
    if (updateDto.username) receptionist.name = updateDto.username;
    if (updateDto.password) receptionist.password = updateDto.password;

    // Save the updated receptionist details
    return this.receptionistRepository.save(receptionist);
  }
}
