import { Controller, Post, Get, Body, Param } from '@nestjs/common';
import { HeadOfficeService } from './head-office.service';
import { CreateHeadOfficeDto } from './dto/CreateHeadOfficeDto.dto';
import { HeadOffice } from './head-office.entity';

@Controller('v1/headoffices')
export class HeadOfficeController {
  constructor(private readonly headofficeService: HeadOfficeService) {}

  @Post()
  async create(@Body() dto: CreateHeadOfficeDto): Promise<HeadOffice> {
    return this.headofficeService.create(dto);
  }

  @Get()
  async findAll(): Promise<HeadOffice[]> {
    return this.headofficeService.findAll();
  }

  @Get(':id')
  async findOne(@Param('id') id: number): Promise<HeadOffice> {
    return this.headofficeService.findOne(id);
  }
}
