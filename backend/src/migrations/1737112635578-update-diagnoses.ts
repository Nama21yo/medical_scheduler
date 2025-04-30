import { MigrationInterface, QueryRunner } from 'typeorm';

export class UpdateDiagnoses1737112635578 implements MigrationInterface {
  name = 'UpdateDiagnoses1737112635578';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`
            ALTER TABLE "branches" DROP CONSTRAINT "FK_d46d4e59de04e0209880558e7dd"
        `);
    await queryRunner.query(`
            ALTER TABLE "diagnoses" DROP COLUMN "resolved"
        `);
    await queryRunner.query(`
            ALTER TABLE "diagnoses"
            ADD "prescription" text
        `);
    await queryRunner.query(`
            ALTER TABLE "diagnoses"
            ADD "visisble" boolean NOT NULL DEFAULT true
        `);
    await queryRunner.query(`
            ALTER TABLE "headoffices" DROP CONSTRAINT "UQ_25446b9e002624bd07aa09487c4"
        `);
    await queryRunner.query(`
            ALTER TABLE "headoffices"
            ALTER COLUMN "contact_email"
            SET NOT NULL
        `);
    await queryRunner.query(`
            ALTER TABLE "headoffices"
            ADD CONSTRAINT "UQ_47cc0ebad670948ede3cb9fb45d" UNIQUE ("contact_email")
        `);
    await queryRunner.query(`
            ALTER TABLE "branches"
            ALTER COLUMN "contact_email"
            SET NOT NULL
        `);
    await queryRunner.query(`
            ALTER TABLE "branches"
            ADD CONSTRAINT "UQ_865168c4a91306a8a14b0eeb8b1" UNIQUE ("contact_email")
        `);
    await queryRunner.query(`
            ALTER TABLE "doctors"
            ADD CONSTRAINT "FK_efbb5ff2cfbc55ca8f22d1006d8" FOREIGN KEY ("branch_id") REFERENCES "branches"("branch_id") ON DELETE
            SET NULL ON UPDATE NO ACTION
        `);
    await queryRunner.query(`
            ALTER TABLE "branches"
            ADD CONSTRAINT "FK_d46d4e59de04e0209880558e7dd" FOREIGN KEY ("headoffice_id") REFERENCES "headoffices"("headoffice_id") ON DELETE CASCADE ON UPDATE NO ACTION
        `);
    await queryRunner.query(`
            ALTER TABLE "receptionists"
            ADD CONSTRAINT "FK_d748ab8b322ba75906dd2d3059c" FOREIGN KEY ("branch_id") REFERENCES "branches"("branch_id") ON DELETE
            SET NULL ON UPDATE NO ACTION
        `);
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`
            ALTER TABLE "receptionists" DROP CONSTRAINT "FK_d748ab8b322ba75906dd2d3059c"
        `);
    await queryRunner.query(`
            ALTER TABLE "branches" DROP CONSTRAINT "FK_d46d4e59de04e0209880558e7dd"
        `);
    await queryRunner.query(`
            ALTER TABLE "doctors" DROP CONSTRAINT "FK_efbb5ff2cfbc55ca8f22d1006d8"
        `);
    await queryRunner.query(`
            ALTER TABLE "branches" DROP CONSTRAINT "UQ_865168c4a91306a8a14b0eeb8b1"
        `);
    await queryRunner.query(`
            ALTER TABLE "branches"
            ALTER COLUMN "contact_email" DROP NOT NULL
        `);
    await queryRunner.query(`
            ALTER TABLE "headoffices" DROP CONSTRAINT "UQ_47cc0ebad670948ede3cb9fb45d"
        `);
    await queryRunner.query(`
            ALTER TABLE "headoffices"
            ALTER COLUMN "contact_email" DROP NOT NULL
        `);
    await queryRunner.query(`
            ALTER TABLE "headoffices"
            ADD CONSTRAINT "UQ_25446b9e002624bd07aa09487c4" UNIQUE ("name")
        `);
    await queryRunner.query(`
            ALTER TABLE "diagnoses" DROP COLUMN "visisble"
        `);
    await queryRunner.query(`
            ALTER TABLE "diagnoses" DROP COLUMN "prescription"
        `);
    await queryRunner.query(`
            ALTER TABLE "diagnoses"
            ADD "resolved" boolean NOT NULL DEFAULT false
        `);
    await queryRunner.query(`
            ALTER TABLE "branches"
            ADD CONSTRAINT "FK_d46d4e59de04e0209880558e7dd" FOREIGN KEY ("headoffice_id") REFERENCES "headoffices"("headoffice_id") ON DELETE
            SET NULL ON UPDATE NO ACTION
        `);
  }
}
