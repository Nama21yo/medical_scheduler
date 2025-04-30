import { MigrationInterface, QueryRunner } from 'typeorm';

export class UpdatePatient1737350048062 implements MigrationInterface {
  name = 'UpdatePatient1737350048062';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`
          ALTER TABLE "patients"
          ADD "password" varchar NULL
        `);
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`
          ALTER TABLE "patients"
          DROP COLUMN "password"
        `);
  }
}
