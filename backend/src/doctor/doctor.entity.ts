import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  ManyToOne,
  CreateDateColumn,
  UpdateDateColumn,
  JoinColumn,
  BeforeInsert,
} from 'typeorm';
import { Branch } from 'src/branch/branch.entity';
import { hash } from 'bcryptjs';

@Entity('doctors')
export class Doctor {
  @PrimaryGeneratedColumn()
  doctor_id: number;

  @Column()
  name: string;

  @Column({ unique: true })
  email: string;

  @Column({ nullable: true })
  specialty: string;

  @Column({ nullable: true })
  password: string;

  @Column({ default: false }) // Indicates if the doctor has signed up
  is_signed_up: boolean;

  @ManyToOne(() => Branch, (branch) => branch.doctors, { onDelete: 'SET NULL' })
  @JoinColumn({ name: 'branch_id' })
  branch: Branch;

  @CreateDateColumn()
  created_at: Date;

  @UpdateDateColumn()
  updated_at: Date;

  @BeforeInsert()
  async hashPassword() {
    if (this.password) {
      this.password = await hash(this.password, 10);
    }
  }
}
