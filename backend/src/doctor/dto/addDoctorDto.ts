import { IsNotEmpty, IsEmail, IsNumber, IsString } from 'class-validator';

export class AddDoctorDto {
  @IsNotEmpty()
  @IsString()
  name: string;

  @IsNotEmpty()
  @IsEmail()
  email: string;

  @IsNotEmpty()
  @IsNumber()
  branch_id: number;
}
