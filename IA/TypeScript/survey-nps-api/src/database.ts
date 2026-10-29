import * as fs from "fs";
import * as path from "path";

import {
    Connection,
    createConnection
} from "typeorm";

import { User } from "./entities/User";
import { Survey } from "./entities/Survey";
import { Invitation } from "./entities/Invitation";

import {
    CreateSchema1610000000000
} from "./migrations/1610000000000-CreateSchema";

export async function openDatabase(): Promise<Connection> {
    const configuredPath =
        process.env.SQLITE_PATH ||
        "./storage/surveys.sqlite";

    const databasePath = path.resolve(
        configuredPath
    );

    const databaseDirectory = path.dirname(
        databasePath
    );

    if (!fs.existsSync(databaseDirectory)) {
        fs.mkdirSync(
            databaseDirectory,
            {
                recursive: true
            }
        );
    }

    return createConnection({
        type: "sqlite",
        database: databasePath,

        entities: [
            User,
            Survey,
            Invitation
        ],

        migrations: [
            CreateSchema1610000000000
        ],

        synchronize: false,
        migrationsRun: false,
        logging: false
    });
}