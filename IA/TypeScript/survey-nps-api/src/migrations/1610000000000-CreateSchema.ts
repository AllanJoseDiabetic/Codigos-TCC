import {
    MigrationInterface,
    QueryRunner,
    Table,
    TableForeignKey,
    TableIndex
} from "typeorm";

export class CreateSchema1610000000000
    implements MigrationInterface {

    public async up(
        queryRunner: QueryRunner
    ): Promise<void> {

        await queryRunner.createTable(
            new Table({
                name: "users",
                columns: [
                    {
                        name: "id",
                        type: "varchar",
                        length: "36",
                        isPrimary: true,
                        isNullable: false
                    },
                    {
                        name: "name",
                        type: "varchar",
                        length: "120",
                        isNullable: false
                    },
                    {
                        name: "email",
                        type: "varchar",
                        length: "254",
                        isNullable: false,
                        isUnique: true
                    },
                    {
                        name: "created_at",
                        type: "datetime",
                        isNullable: false,
                        default: "CURRENT_TIMESTAMP"
                    }
                ]
            }),
            true
        );

        await queryRunner.createTable(
            new Table({
                name: "surveys",
                columns: [
                    {
                        name: "id",
                        type: "varchar",
                        length: "36",
                        isPrimary: true,
                        isNullable: false
                    },
                    {
                        name: "title",
                        type: "varchar",
                        length: "180",
                        isNullable: false
                    },
                    {
                        name: "description",
                        type: "text",
                        isNullable: false
                    },
                    {
                        name: "created_at",
                        type: "datetime",
                        isNullable: false,
                        default: "CURRENT_TIMESTAMP"
                    }
                ]
            }),
            true
        );

        await queryRunner.createTable(
            new Table({
                name: "invitations",
                columns: [
                    {
                        name: "id",
                        type: "varchar",
                        length: "36",
                        isPrimary: true,
                        isNullable: false
                    },
                    {
                        name: "user_id",
                        type: "varchar",
                        length: "36",
                        isNullable: false
                    },
                    {
                        name: "survey_id",
                        type: "varchar",
                        length: "36",
                        isNullable: false
                    },
                    {
                        name: "value",
                        type: "integer",
                        isNullable: true
                    },
                    {
                        name: "sent_at",
                        type: "datetime",
                        isNullable: false,
                        default: "CURRENT_TIMESTAMP"
                    },
                    {
                        name: "answered_at",
                        type: "datetime",
                        isNullable: true
                    }
                ]
            }),
            true
        );

        await queryRunner.createForeignKey(
            "invitations",
            new TableForeignKey({
                name: "FK_invitations_user",
                columnNames: ["user_id"],
                referencedTableName: "users",
                referencedColumnNames: ["id"],
                onDelete: "CASCADE"
            })
        );

        await queryRunner.createForeignKey(
            "invitations",
            new TableForeignKey({
                name: "FK_invitations_survey",
                columnNames: ["survey_id"],
                referencedTableName: "surveys",
                referencedColumnNames: ["id"],
                onDelete: "CASCADE"
            })
        );

        await queryRunner.createIndex(
            "invitations",
            new TableIndex({
                name: "IDX_invitations_survey_value",
                columnNames: [
                    "survey_id",
                    "value"
                ]
            })
        );

        await queryRunner.createIndex(
            "invitations",
            new TableIndex({
                name: "IDX_invitations_user_survey",
                columnNames: [
                    "user_id",
                    "survey_id"
                ]
            })
        );
    }

    public async down(
        queryRunner: QueryRunner
    ): Promise<void> {

        await queryRunner.dropTable(
            "invitations",
            true
        );

        await queryRunner.dropTable(
            "surveys",
            true
        );

        await queryRunner.dropTable(
            "users",
            true
        );
    }
}