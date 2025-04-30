import { IsEmail, IsNotEmpty, IsString } from 'class-validator';

export class PatientSignupDto {
  @IsEmail()
  email: string;

  @IsString()
  @IsNotEmpty()
  username: string;

  @IsString()
  @IsNotEmpty()
  password: string;
}
