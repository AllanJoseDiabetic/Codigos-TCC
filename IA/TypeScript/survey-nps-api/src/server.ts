import "reflect-metadata";

import app from "./app";

import { openDatabase } from "./database";

async function start(): Promise<void> {

    const port = Number(
        process.env.PORT || "3333"
    );

    if (
        !Number.isInteger(port) ||
        port < 1 ||
        port > 65535
    ) {
        throw new Error(
            "A variável PORT deve indicar " +
            "uma porta TCP válida."
        );
    }

    await openDatabase();

    app.listen(
        port,
        function () {
            console.log(
                "API de pesquisas disponível " +
                "na porta " + port + "."
            );
        }
    );
}

start().catch(function (error) {
    console.error(
        "Falha na inicialização:",
        error.message
    );

    process.exitCode = 1;
});