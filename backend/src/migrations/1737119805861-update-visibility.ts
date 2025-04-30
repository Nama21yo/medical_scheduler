import { MigrationInterface, QueryRunner } from "typeorm";

export class UpdateVisibility1737119805861 implements MigrationInterface {
    name = 'UpdateVisibility1737119805861'

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`
            ALTER TABLE "diagnoses"
            ALTER COLUMN "visisble"
            SET DEFAULT true
        `);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`
            ALTER TABLE "diagnoses"
            ALTER COLUMN "visisble"
            SET DEFAULT false
        `);
    }

}
