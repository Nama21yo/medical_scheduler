import { MigrationInterface, QueryRunner } from "typeorm";

export class UpdateStatusQueue1737204699829 implements MigrationInterface {
    name = 'UpdateStatusQueue1737204699829'

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`
            ALTER TABLE "queues" DROP COLUMN "status"
        `);
        await queryRunner.query(`
            ALTER TABLE "queues"
            ADD "status" integer NOT NULL DEFAULT '1'
        `);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`
            ALTER TABLE "queues" DROP COLUMN "status"
        `);
        await queryRunner.query(`
            ALTER TABLE "queues"
            ADD "status" boolean NOT NULL DEFAULT true
        `);
    }

}
