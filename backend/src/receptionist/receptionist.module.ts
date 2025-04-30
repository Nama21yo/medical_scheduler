import { Module } from '@nestjs/common';
import { ReceptionistService } from './receptionist.service';
import { ReceptionistController } from './receptionist.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Receptionist } from './receptionist.entity';
import { Branch } from 'src/branch/branch.entity';
import { JwtModule } from '@nestjs/jwt';

@Module({
  imports: [TypeOrmModule.forFeature([Receptionist, Branch]), JwtModule],
  providers: [ReceptionistService],
  controllers: [ReceptionistController],
  exports: [ReceptionistService],
})
export class ReceptionistModule {}
