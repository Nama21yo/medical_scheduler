import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { HeadOffice } from './head-office.entity';
import { CreateHeadOfficeDto } from './dto/CreateHeadOfficeDto.dto';

@Injectable()
export class HeadOfficeService {
  constructor(
    @InjectRepository(HeadOffice)
    private readonly headofficeRepository: Repository<HeadOffice>,
  ) {}

  async create(dto: CreateHeadOfficeDto): Promise<HeadOffice> {
    const headoffice = this.headofficeRepository.create(dto);
    return this.headofficeRepository.save(headoffice);
  }

  async findAll(): Promise<HeadOffice[]> {
    return this.headofficeRepository.find({ relations: ['branches'] });
  }

  async findOne(id: number): Promise<HeadOffice> {
    const headoffice = await this.headofficeRepository.findOne({
      where: { headoffice_id: id },
      relations: ['branches'],
    });

    if (!headoffice) {
      throw new NotFoundException(`Head Office with ID ${id} not found`);
    }
    return headoffice;
  }
}
