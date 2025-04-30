import { Module } from '@nestjs/common';
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from './user.entity';
import { RolesModule } from 'src/roles/roles.module';
import { JwtModule } from '@nestjs/jwt';
import { Token } from './token.entity';
import { TokenService } from './token.service';
import { BranchModule } from 'src/branch/branch.module';
import { DoctorModule } from 'src/doctor/doctor.module';
import { ReceptionistModule } from 'src/receptionist/receptionist.module';
import { PatientModule } from 'src/patient/patient.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([User, Token]), // Register User and Token entities
    RolesModule,
    JwtModule.register({
      secret: 'secret', // Use an environment variable in production
      signOptions: { expiresIn: '1w' },
    }),
    BranchModule, // Add BranchModule here
    DoctorModule, // Add DoctorModule for DoctorService
    ReceptionistModule, // Add ReceptionistModule for ReceptionistService
    PatientModule,
  ],
  controllers: [UserController],
  providers: [UserService, TokenService],
  exports: [UserService, TokenService],
})
export class UserModule {}
