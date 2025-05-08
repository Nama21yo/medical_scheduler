import {
  Injectable,
  NotFoundException,
  BadRequestException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Doctor } from './doctor.entity';
import { AddDoctorDto } from './dto/addDoctorDto';
import { DoctorSignupDto } from './dto/doctorSignUp.dto';
import { Branch } from 'src/branch/branch.entity';
import { UpdateUserDto } from 'src/user/dto/updateUser.dto';

@Injectable()
export class DoctorService {
  constructor(
    @InjectRepository(Doctor)
    private readonly doctorRepository: Repository<Doctor>,
    @InjectRepository(Branch)
    private readonly branchRepository: Repository<Branch>,
  ) {}

  async addDoctor(dto: AddDoctorDto): Promise<Doctor> {
    const branch = await this.branchRepository.findOne({
      where: { branch_id: dto.branch_id },
    });
    if (!branch) {
      throw new NotFoundException(`Branch with ID ${dto.branch_id} not found`);
    }

    const doctor = this.doctorRepository.create({
      name: dto.name,
      email: dto.email,
      branch,
    });
    return this.doctorRepository.save(doctor);
  }

  async findAll(): Promise<Doctor[]> {
    return this.doctorRepository.find({ relations: ['branch'] });
  }

  async signup(email: string, dto: DoctorSignupDto): Promise<Doctor> {
    const doctor = await this.doctorRepository.findOne({ where: { email } });

    if (!doctor) {
      throw new NotFoundException(`Doctor with email ${email} not found`);
    }

    if (doctor.is_signed_up) {
      throw new BadRequestException(
        `Doctor with email ${email} has already signed up`,
      );
    }

    doctor.name = dto.name;
    doctor.specialty = dto.specialty;
    doctor.password = dto.password;
    doctor.is_signed_up = true;

    return this.doctorRepository.save(doctor);
  }
  async findByEmail(email: string): Promise<any> {
    const doctor = await this.doctorRepository.findOne({ where: { email } });

    if (!doctor) {
      throw new BadRequestException('Doctor with this email does not exist');
    }

    return doctor;
  }

  async update(email: string, updateDto: UpdateUserDto): Promise<any> {
    const doctor = await this.findByEmail(email);
    if (!doctor) {
      throw new BadRequestException('Doctor not found');
    }

    // Update doctor-specific fields
    if (updateDto.username) doctor.name = updateDto.username;
    if (updateDto.password) doctor.password = updateDto.password;

    // Save the updated doctor details
    return this.doctorRepository.save(doctor);
  }

  async deleteByEmail(email: string): Promise<void> {
    const doctor = await this.doctorRepository.findOne({ where: { email } });
    if (!doctor) {
      throw new NotFoundException('Doctor Not Found');
    }

    await this.doctorRepository.remove(doctor);
  }
}
