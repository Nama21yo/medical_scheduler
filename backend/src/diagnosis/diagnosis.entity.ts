import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  ManyToOne,
  CreateDateColumn,
  UpdateDateColumn,
  JoinColumn,
} from 'typeorm';
import { Patient } from 'src/patient/patient.entity';
import { User } from 'src/user/user.entity';

@Entity('diagnoses')
export class Diagnosis {
  @PrimaryGeneratedColumn()
  diagnosis_id: number;
  // prescription
  @Column({ type: 'text' })
  diagnosis_name: string;

  @Column({ type: 'text', nullable: true })
  diagnosis_details: string;

  @Column({ type: 'text', nullable: true })
  prescription: string;

  @ManyToOne(() => Patient, { nullable: false, onDelete: 'CASCADE' })
  @JoinColumn({ name: 'patient_id' })
  patient: Patient;

  @ManyToOne(() => User, { nullable: false, onDelete: 'SET NULL' })
  @JoinColumn({ name: 'doctor_id' })
  doctor: User;

  @Column({ default: true }) // Whether the diagnosis has been resolved
  visible: boolean;
  // visible

  @CreateDateColumn()
  created_at: Date;

  @UpdateDateColumn()
  updated_at: Date;
}
