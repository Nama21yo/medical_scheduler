import { MigrationInterface, QueryRunner } from "typeorm";

export class UpdateVisibleColumn1737145565612 implements MigrationInterface {
    name = 'UpdateVisibleColumn1737145565612'

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`
            ALTER TABLE "diagnoses"
                RENAME COLUMN "visisble" TO "visible"
        `);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`
            ALTER TABLE "diagnoses"
                RENAME COLUMN "visible" TO "visisble"
        `);
    }

}
