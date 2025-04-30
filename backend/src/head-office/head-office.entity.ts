import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  OneToMany,
  CreateDateColumn,
  UpdateDateColumn,
} from 'typeorm';
import { Branch } from 'src/branch/branch.entity';

@Entity('headoffices')
export class HeadOffice {
  @PrimaryGeneratedColumn()
  headoffice_id: number;

  @Column()
  name: string;

  @Column()
  location: string;

  @Column({ unique: true })
  contact_email: string;

  @Column({ nullable: true })
  contact_phone: string;

  // Define One-to-Many relationship with Branch
  @OneToMany(() => Branch, (branch) => branch.headoffice)
  branches: Branch[];

  @CreateDateColumn()
  created_at: Date;

  @UpdateDateColumn()
  updated_at: Date;
}
