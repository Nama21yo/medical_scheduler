import { Controller, Post, Get, Body, Param, UseGuards } from '@nestjs/common';
import { PatientService } from './patient.service';
import { CreatePatientDto } from './dto/createPatient.dto';
import { Patient } from './patient.entity';
import { RolesGuard } from 'src/roles/roles.guard';
import { Roles } from 'src/roles/decorator/roles.decorator';

@Controller('v1/patients')
@UseGuards(RolesGuard)
export class PatientController {
  constructor(private readonly patientService: PatientService) {}

  /**
   * @route POST /v1/patients
   * @description Register a new patient
   * @param createPatientDto - DTO for patient creation
   * @returns The created Patient object
   */
  @Post()
  // @Roles('receptionist', 'branch', 'headoffice')
  async register(@Body() createPatientDto: CreatePatientDto): Promise<Patient> {
    return this.patientService.register(createPatientDto);
  }

  /**
   * @route GET /v1/patients/:id
   * @description Get details of a specific patient
   * @param id - Patient ID
   * @returns The Patient object
   */
  @Get(':id')
  async getPatient(@Param('id') id: number): Promise<Patient> {
    return this.patientService.findById(id);
  }

  /**
   * @route GET /v1/patients
   * @description Get all registered patients
   * @returns List of Patient objects
   */
  @Get()
  async getAllPatients(): Promise<Patient[]> {
    return this.patientService.findAll();
  }
}
