import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  ManyToOne,
  OneToMany,
  CreateDateColumn,
  UpdateDateColumn,
  JoinColumn,
} from 'typeorm';
import { Doctor } from 'src/doctor/doctor.entity';
import { Receptionist } from 'src/receptionist/receptionist.entity';
import { HeadOffice } from 'src/head-office/head-office.entity';

@Entity('branches')
export class Branch {
  @PrimaryGeneratedColumn()
  branch_id: number;

  @Column()
  name: string;

  @Column()
  location: string;

  @Column({ unique: true })
  contact_email: string;

  @Column({ nullable: true })
  contact_phone: string;

  @Column({ nullable: true })
  password: string; // Password for branch signup

  @Column({ nullable: true })
  specialization: string;

  @Column({ default: false })
  is_signed_up: boolean; // Tracks if branch completed signup

  // Many-to-One relationship with HeadOffice
  @ManyToOne(() => HeadOffice, (headoffice) => headoffice.branches, {
    onDelete: 'CASCADE',
  })
  @JoinColumn({ name: 'headoffice_id' })
  headoffice: HeadOffice;

  // Define One-to-Many relationship with Doctor
  @OneToMany(() => Doctor, (doctor) => doctor.branch)
  doctors: Doctor[];

  // Define One-to-Many relationship with Receptionist
  @OneToMany(() => Receptionist, (receptionist) => receptionist.branch)
  receptionists: Receptionist[];

  @CreateDateColumn()
  created_at: Date;

  @UpdateDateColumn()
  updated_at: Date;
}

// remove specialization
