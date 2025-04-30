import { Test, TestingModule } from '@nestjs/testing';
import { HeadOfficeService } from './head-office.service';

describe('HeadOfficeService', () => {
  let service: HeadOfficeService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [HeadOfficeService],
    }).compile();

    service = module.get<HeadOfficeService>(HeadOfficeService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
