import {
  IsNotEmpty,
  IsOptional,
  IsEmail,
  IsString,
  IsNumber,
} from 'class-validator';

export class CreateBranchDto {
  @IsNotEmpty()
  @IsString()
  name: string;

  // yitefa
  @IsNotEmpty()
  @IsString()
  location: string;

  @IsOptional()
  @IsEmail()
  contact_email?: string;

  // yitefa
  @IsOptional()
  @IsString()
  contact_phone?: string;

  @IsNotEmpty()
  @IsNumber()
  headoffice_id: number;
}
