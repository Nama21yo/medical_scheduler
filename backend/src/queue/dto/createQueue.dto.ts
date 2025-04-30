import { IsNotEmpty, IsNumber } from 'class-validator';

export class CreateQueueDto {
  @IsNotEmpty()
  @IsNumber()
  patient_id: number;

  @IsNotEmpty()
  @IsNumber()
  doctor_id: number;

  @IsNumber()
  status: number; // Optional status; defaults to 1 (Unpending) if not provided
}
