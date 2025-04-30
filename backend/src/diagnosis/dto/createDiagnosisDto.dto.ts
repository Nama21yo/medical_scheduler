import {
  IsNotEmpty,
  IsString,
  IsOptional,
  IsNumber,
  IsBoolean,
} from 'class-validator';

export class CreateDiagnosisDto {
  @IsNotEmpty()
  @IsString()
  diagnosis_name: string;

  @IsOptional()
  @IsString()
  diagnosis_details?: string;

  @IsOptional()
  @IsString()
  prescription?: string;

  @IsNotEmpty()
  @IsNumber()
  patient_id: number;

  @IsNotEmpty()
  @IsNumber()
  doctor_id: number;

  @IsOptional()
  @IsBoolean()
  visible?: boolean;
}
