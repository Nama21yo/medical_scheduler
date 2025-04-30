import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Branch } from './branch.entity';
import { BranchService } from './branch.service';
import { BranchController } from './branch.controller';
import { HeadOffice } from 'src/head-office/head-office.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Branch, HeadOffice])],
  controllers: [BranchController],
  providers: [BranchService],
  exports: [BranchService],
})
export class BranchModule {}
