import {
  Controller,
  Get,
  Post,
  Put,
  Delete,
  Param,
  Body,
} from '@nestjs/common';
import { DiagnosisService } from './diagnosis.service';
import { CreateDiagnosisDto } from './dto/createDiagnosisDto.dto';
import { UpdateDiagnosisDto } from './dto/updateDisgnosisDto.dto';
import { Diagnosis } from './diagnosis.entity';

@Controller('v1/diagnoses')
export class DiagnosisController {
  constructor(private readonly diagnosisService: DiagnosisService) {}

  @Get()
  async findAll(): Promise<Diagnosis[]> {
    return this.diagnosisService.findAll();
  }

  @Get(':id')
  async findOne(@Param('id') id: number): Promise<Diagnosis> {
    return this.diagnosisService.findOne(id);
  }

  @Post()
  async create(@Body() dto: CreateDiagnosisDto): Promise<Diagnosis> {
    return this.diagnosisService.create(dto);
  }

  @Put(':id')
  async update(
    @Param('id') id: number,
    @Body() dto: UpdateDiagnosisDto,
  ): Promise<Diagnosis> {
    return this.diagnosisService.update(id, dto);
  }

  @Delete(':id')
  async remove(@Param('id') id: number): Promise<void> {
    return this.diagnosisService.remove(id);
  }
}
