import { MigrationInterface, QueryRunner } from 'typeorm';

export class CreateDiagnosisTable1736773367732 implements MigrationInterface {
  name = 'CreateDiagnosisTable1736773367732';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`
            CREATE TABLE "queues" (
                "queue_id" SERIAL NOT NULL,
                "status" boolean NOT NULL DEFAULT true,
                "created_at" TIMESTAMP NOT NULL DEFAULT now(),
                "updated_at" TIMESTAMP NOT NULL DEFAULT now(),
                "patient_id" integer NOT NULL,
                "doctor_id" integer NOT NULL,
                CONSTRAINT "PK_9c86e57455f479c205741ecb942" PRIMARY KEY ("queue_id")
            )
        `);
    await queryRunner.query(`
            CREATE TABLE IF NOT EXISTS "doctors" (
                "doctor_id" SERIAL NOT NULL,
                "name" character varying NOT NULL,
                "email" character varying NOT NULL,
                "specialty" character varying,
                "password" character varying,
                "is_signed_up" boolean NOT NULL DEFAULT false,
                "created_at" TIMESTAMP NOT NULL DEFAULT now(),
                "updated_at" TIMESTAMP NOT NULL DEFAULT now(),
                "branch_id" integer,
                CONSTRAINT "UQ_62069f52ebba471c91de5d59d61" UNIQUE ("email"),
                CONSTRAINT "PK_24821d9468276ddc40107fc0819" PRIMARY KEY ("doctor_id")
            )
        `);
    await queryRunner.query(`
            CREATE TABLE IF NOT EXISTS "receptionists" (
                "receptionist_id" SERIAL NOT NULL,
                "email" character varying NOT NULL,
                "name" character varying,
                "password" character varying,
                "is_signed_up" boolean NOT NULL DEFAULT false,
                "created_at" TIMESTAMP NOT NULL DEFAULT now(),
                "updated_at" TIMESTAMP NOT NULL DEFAULT now(),
                "branch_id" integer,
                CONSTRAINT "UQ_b13ebea299899ba2bda723de641" UNIQUE ("email"),
                CONSTRAINT "PK_0447af41186e3e759bff66c9385" PRIMARY KEY ("receptionist_id")
            )
        `);
    await queryRunner.query(`
            CREATE TABLE "branches" (
                "branch_id" SERIAL NOT NULL,
                "name" character varying NOT NULL,
                "location" character varying NOT NULL,
                "contact_email" character varying,
                "contact_phone" character varying,
                "password" character varying,
                "is_signed_up" boolean NOT NULL DEFAULT false,
                "created_at" TIMESTAMP NOT NULL DEFAULT now(),
                "updated_at" TIMESTAMP NOT NULL DEFAULT now(),
                "headoffice_id" integer,
                CONSTRAINT "PK_cffb054eec523921707bd442bd9" PRIMARY KEY ("branch_id")
            )
        `);
    await queryRunner.query(`
            CREATE TABLE "headoffices" (
                "headoffice_id" SERIAL NOT NULL,
                "name" character varying NOT NULL,
                "location" character varying NOT NULL,
                "contact_email" character varying,
                "contact_phone" character varying,
                "created_at" TIMESTAMP NOT NULL DEFAULT now(),
                "updated_at" TIMESTAMP NOT NULL DEFAULT now(),
                CONSTRAINT "UQ_25446b9e002624bd07aa09487c4" UNIQUE ("name"),
                CONSTRAINT "PK_81f8a64eb1e0b619c81e1947108" PRIMARY KEY ("headoffice_id")
            )
        `);
    await queryRunner.query(`
            CREATE TABLE "diagnoses" (
                "diagnosis_id" SERIAL NOT NULL,
                "diagnosis_name" text NOT NULL,
                "diagnosis_details" text,
                "resolved" boolean NOT NULL DEFAULT false,
                "created_at" TIMESTAMP NOT NULL DEFAULT now(),
                "updated_at" TIMESTAMP NOT NULL DEFAULT now(),
                "patient_id" integer NOT NULL,
                "doctor_id" integer NOT NULL,
                CONSTRAINT "PK_965d034342acee772a176e84531" PRIMARY KEY ("diagnosis_id")
            )
        `);
    await queryRunner.query(`
            ALTER TABLE "queues"
            ADD CONSTRAINT "FK_f7edfc4fe9f2a59335dde80ab6f" FOREIGN KEY ("patient_id") REFERENCES "patients"("patient_id") ON DELETE CASCADE ON UPDATE NO ACTION
        `);
    await queryRunner.query(`
            ALTER TABLE "queues"
            ADD CONSTRAINT "FK_8b7361114408cbadbe6fc91ff40" FOREIGN KEY ("doctor_id") REFERENCES "user"("user_id") ON DELETE CASCADE ON UPDATE NO ACTION
        `);
    await queryRunner.query(`
            ALTER TABLE "branches"
            ADD CONSTRAINT "FK_d46d4e59de04e0209880558e7dd" FOREIGN KEY ("headoffice_id") REFERENCES "headoffices"("headoffice_id") ON DELETE
            SET NULL ON UPDATE NO ACTION
        `);
    await queryRunner.query(`
            ALTER TABLE "diagnoses"
            ADD CONSTRAINT "FK_2fd88259749f52cc0be1d1f7402" FOREIGN KEY ("patient_id") REFERENCES "patients"("patient_id") ON DELETE CASCADE ON UPDATE NO ACTION
        `);
    await queryRunner.query(`
            ALTER TABLE "diagnoses"
            ADD CONSTRAINT "FK_cf802fcb9f0e60770ea21475f23" FOREIGN KEY ("doctor_id") REFERENCES "user"("user_id") ON DELETE
            SET NULL ON UPDATE NO ACTION
        `);
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`
            ALTER TABLE "diagnoses" DROP CONSTRAINT "FK_cf802fcb9f0e60770ea21475f23"
        `);
    await queryRunner.query(`
            ALTER TABLE "diagnoses" DROP CONSTRAINT "FK_2fd88259749f52cc0be1d1f7402"
        `);
    await queryRunner.query(`
            ALTER TABLE "branches" DROP CONSTRAINT "FK_d46d4e59de04e0209880558e7dd"
        `);
    await queryRunner.query(`
            ALTER TABLE "queues" DROP CONSTRAINT "FK_8b7361114408cbadbe6fc91ff40"
        `);
    await queryRunner.query(`
            ALTER TABLE "queues" DROP CONSTRAINT "FK_f7edfc4fe9f2a59335dde80ab6f"
        `);
    await queryRunner.query(`
            DROP TABLE "diagnoses"
        `);
    await queryRunner.query(`
            DROP TABLE "headoffices"
        `);
    await queryRunner.query(`
            DROP TABLE "branches"
        `);
    await queryRunner.query(`
            DROP TABLE "queues"
        `);
  }
}
