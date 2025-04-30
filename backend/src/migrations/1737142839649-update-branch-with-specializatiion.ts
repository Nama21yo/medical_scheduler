import { MigrationInterface, QueryRunner } from "typeorm";

export class UpdateBranchWithSpecializatiion1737142839649 implements MigrationInterface {
    name = 'UpdateBranchWithSpecializatiion1737142839649'

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`
            ALTER TABLE "branches"
            ADD "specialization" character varying
        `);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`
            ALTER TABLE "branches" DROP COLUMN "specialization"
        `);
    }

}
