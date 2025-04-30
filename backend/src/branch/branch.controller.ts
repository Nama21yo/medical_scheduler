import {
  Controller,
  Post,
  Get,
  Body,
  Param,
  Delete,
  UseGuards,
} from '@nestjs/common';
import { BranchService } from './branch.service';
import { CreateBranchDto } from './dto/CreateBranchDto.dto';
import { BranchSignupDto } from './dto/BranchSignUpDto.dto';
import { Branch } from './branch.entity';
import { RolesGuard } from 'src/roles/roles.guard';
import { Roles } from 'src/roles/decorator/roles.decorator';

@Controller('v1/branches')
export class BranchController {
  constructor(private readonly branchService: BranchService) {}

  /**
   * @route POST /v1/branches
   * @description Add a new branch (by HeadOffice)
   */
  @Post()
  async create(@Body() dto: CreateBranchDto): Promise<Branch> {
    return this.branchService.create(dto);
  }

  /**
   * @route POST /v1/branches/signup
   * @description Sign up as a branch
   */
  // @Post('signup/:email')
  // async signup(
  //   @Param('email') email: string,
  //   @Body() dto: BranchSignupDto,
  // ): Promise<Branch> {
  //   return this.branchService.signup(email, dto);
  // }

  /**
   * @route GET /v1/branches
   * @description Get all branches
   */
  @Get()
  async findAll(): Promise<Branch[]> {
    return this.branchService.findAll();
  }

  /**
   * @route DELETE /v1/branches/:id
   * @description Delete a branch by ID
   */
  @Delete(':id')
  async delete(@Param('id') id: number): Promise<void> {
    return this.branchService.delete(id);
  }
}
