import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { HeadOffice } from './head-office.entity';
import { HeadOfficeService } from './head-office.service';
import { HeadOfficeController } from './head-office.controller';

@Module({
  imports: [TypeOrmModule.forFeature([HeadOffice])],
  controllers: [HeadOfficeController],
  providers: [HeadOfficeService],
  exports: [HeadOfficeService],
})
export class HeadOfficeModule {}
