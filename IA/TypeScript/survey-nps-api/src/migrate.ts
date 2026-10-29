import "reflect-metadata";

import { openDatabase } from "./database";

async function migrate(): Promise<void> {
    const connection = await openDatabase();

    try {
        const migrations = await connection.runMigrations();

        console.log(
            "Migrações aplicadas:",
            migrations.length
        );
    } finally {
        await connection.close();
    }
}

migrate().catch(function (error) {
    console.error(
        "Falha ao aplicar migrações:",
        error.message
    );

    process.exitCode = 1;
});