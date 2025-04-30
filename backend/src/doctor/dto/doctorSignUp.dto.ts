import { IsNotEmpty, IsString } from 'class-validator';

export class DoctorSignupDto {
  @IsNotEmpty()
  @IsString()
  name: string;

  @IsNotEmpty()
  @IsString()
  specialty: string;

  @IsNotEmpty()
  @IsString()
  password: string;
}
