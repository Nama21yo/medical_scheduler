import { IsOptional, IsString, IsBoolean } from 'class-validator';

export class UpdateDiagnosisDto {
  @IsOptional()
  @IsString()
  diagnosis_name?: string;

  @IsOptional()
  @IsString()
  diagnosis_details?: string;

  @IsOptional()
  @IsString()
  prescription?: string;

  @IsOptional()
  @IsBoolean()
  visible?: boolean;
}
