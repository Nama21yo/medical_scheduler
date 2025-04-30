import { Test, TestingModule } from '@nestjs/testing';
import { HeadOfficeController } from './head-office.controller';

describe('HeadOfficeController', () => {
  let controller: HeadOfficeController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [HeadOfficeController],
    }).compile();

    controller = module.get<HeadOfficeController>(HeadOfficeController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
