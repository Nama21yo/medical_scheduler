import { IsNotEmpty, IsOptional, IsEmail, IsString } from 'class-validator';

export class CreateHeadOfficeDto {
  @IsNotEmpty()
  @IsString()
  name: string;

  @IsNotEmpty()
  @IsString()
  location: string;

  @IsOptional()
  @IsEmail()
  contact_email?: string;

  @IsOptional()
  @IsString()
  contact_phone?: string;
}
