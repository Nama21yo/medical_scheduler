import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Diagnosis } from './diagnosis.entity';
import { CreateDiagnosisDto } from './dto/createDiagnosisDto.dto';
import { UpdateDiagnosisDto } from './dto/updateDisgnosisDto.dto';
import { Patient } from 'src/patient/patient.entity';
import { User } from 'src/user/user.entity';

@Injectable()
export class DiagnosisService {
  constructor(
    @InjectRepository(Diagnosis)
    private readonly diagnosisRepository: Repository<Diagnosis>,
    @InjectRepository(Patient)
    private readonly patientRepository: Repository<Patient>,
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,
  ) {}

  async findAll(): Promise<Diagnosis[]> {
    return this.diagnosisRepository.find({ relations: ['patient', 'doctor'] });
  }

  async findOne(id: number): Promise<Diagnosis> {
    const diagnosis = await this.diagnosisRepository.findOne({
      where: { diagnosis_id: id },
      relations: ['patient', 'doctor'],
    });

    if (!diagnosis) {
      throw new NotFoundException(`Diagnosis with ID ${id} not found`);
    }

    return diagnosis;
  }

  async create(dto: CreateDiagnosisDto): Promise<Diagnosis> {
    const { patient_id, doctor_id, ...diagnosisData } = dto;

    const patient = await this.patientRepository.findOne({
      where: { patient_id },
    });
    if (!patient) {
      throw new NotFoundException(`Patient with ID ${patient_id} not found`);
    }

    const doctor = await this.userRepository.findOne({
      where: { user_id: doctor_id },
    });
    if (!doctor) {
      throw new NotFoundException(`Doctor with ID ${doctor_id} not found`);
    }

    const diagnosis = this.diagnosisRepository.create({
      ...diagnosisData,
      patient,
      doctor,
    });

    return this.diagnosisRepository.save(diagnosis);
  }

  async update(id: number, dto: UpdateDiagnosisDto): Promise<Diagnosis> {
    const diagnosis = await this.findOne(id);

    if (dto.diagnosis_name) diagnosis.diagnosis_name = dto.diagnosis_name;
    if (dto.diagnosis_details)
      diagnosis.diagnosis_details = dto.diagnosis_details;
    if (dto.prescription) diagnosis.prescription = dto.prescription;
    if (dto.visible !== undefined) diagnosis.visible = dto.visible;

    return this.diagnosisRepository.save(diagnosis);
  }

  async remove(id: number): Promise<void> {
    const diagnosis = await this.findOne(id);
    await this.diagnosisRepository.remove(diagnosis);
  }
}
