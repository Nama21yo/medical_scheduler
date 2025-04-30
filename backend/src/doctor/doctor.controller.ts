import { Controller, Post, Body, Param, UseGuards, Get } from '@nestjs/common';
import { DoctorService } from './doctor.service';
import { AddDoctorDto } from './dto/addDoctorDto';
import { DoctorSignupDto } from './dto/doctorSignUp.dto';
import { Doctor } from './doctor.entity';
import { RolesGuard } from 'src/roles/roles.guard';
import { Roles } from 'src/roles/decorator/roles.decorator';

@Controller('v1/doctors')
@UseGuards(RolesGuard)
export class DoctorController {
  constructor(private readonly doctorService: DoctorService) {}

  @Post()
  @Roles('headoffice', 'branch')
  async addDoctor(@Body() dto: AddDoctorDto): Promise<Doctor> {
    return this.doctorService.addDoctor(dto);
  }

  // @Post('signup/:email')
  // async signup(
  //   @Param('email') email: string,
  //   @Body() dto: DoctorSignupDto,
  // ): Promise<Doctor> {
  //   return this.doctorService.signup(email, dto);
  // }
  // @Get()
  // async findAll(): Promise<Branch[]> {
  //   return this.branchService.findAll();
  // }
  @Get()
  async findAll(): Promise<Doctor[]> {
    return this.doctorService.findAll();
  }
}
