import { IsNotEmpty, IsString } from 'class-validator';

export class ReceptionistSignupDto {
  @IsNotEmpty()
  @IsString()
  password: string;

  @IsNotEmpty()
  @IsString()
  name: string;
}
