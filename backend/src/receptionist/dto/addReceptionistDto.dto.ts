import { IsNotEmpty, IsEmail, IsNumber } from 'class-validator';

export class AddReceptionistDto {
  @IsNotEmpty()
  @IsEmail()
  email: string;

  @IsNotEmpty()
  @IsNumber()
  branch_id: number;
}
