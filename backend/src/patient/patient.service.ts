import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Patient } from './patient.entity';
import { CreatePatientDto } from './dto/createPatient.dto';
import { User } from 'src/user/user.entity';
import { PatientSignupDto } from './dto/patientSignUp.dto';

@Injectable()
export class PatientService {
  constructor(
    @InjectRepository(Patient)
    private readonly patientRepository: Repository<Patient>,
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,
  ) {}

  /**
   * @description Register a new patient
   * @param createPatientDto - Data for patient creation
   * @returns The created Patient object
   */
  async register(createPatientDto: CreatePatientDto): Promise<Patient> {
    const { registered_by_id, ...patientData } = createPatientDto;

    const registeredBy = await this.userRepository.findOne({
      where: { user_id: registered_by_id },
    });

    if (!registeredBy) {
      throw new NotFoundException(
        `Receptionist with ID ${registered_by_id} not found`,
      );
    }

    const newPatient = this.patientRepository.create({
      ...patientData,
      registered_by: registeredBy,
    });

    return this.patientRepository.save(newPatient);
  }

  async signup(email: string, username: string): Promise<Patient> {
    const [first_name, last_name] = username.split('.'); // Split username into first_name and last_name

    const patient = this.patientRepository.create({
      email,
      first_name,
      last_name,
    });

    return this.patientRepository.save(patient);
  }

  async findByEmail(email: string): Promise<Patient | undefined> {
    return this.patientRepository.findOne({ where: { email } });
  }

  async update(patient: Patient): Promise<Patient> {
    return this.patientRepository.save(patient);
  }

  /**
   * @description Find a patient by ID
   * @param id - Patient ID
   * @returns The Patient object
   */
  async findById(id: number): Promise<Patient> {
    const patient = await this.patientRepository.findOne({
      where: { patient_id: id },
      relations: ['registered_by'],
    });

    if (!patient) {
      throw new NotFoundException(`Patient with ID ${id} not found`);
    }

    return patient;
  }

  /**
   * @description Find all registered patients
   * @returns List of all Patient objects
   */
  async findAll(): Promise<Patient[]> {
    return this.patientRepository.find({ relations: ['registered_by'] });
  }

  async deleteByEmail(email: string): Promise<void> {
    const patient = await this.patientRepository.findOne({ where: { email } });

    if (!patient) {
      throw new NotFoundException('Patient not Found');
    }

    await this.patientRepository.remove(patient);
  }
}
