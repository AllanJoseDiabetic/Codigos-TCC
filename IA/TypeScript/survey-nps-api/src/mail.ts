import * as nodemailer from "nodemailer";
import * as Handlebars from "handlebars";

import { User } from "./entities/User";
import { Survey } from "./entities/Survey";
import { Invitation } from "./entities/Invitation";

interface RatingOption {
    value: number;
    url: string;
}

export interface MailDelivery {
    mode: string;
    preview: string | null;
}

const invitationTemplate = Handlebars.compile(
    [
        "<!DOCTYPE html>",
        "<html lang=\"pt-BR\">",
        "<head>",
        "<meta charset=\"UTF-8\">",
        "<title>Pesquisa de satisfação</title>",
        "</head>",
        "<body style=\"margin:0;padding:24px;background:#f2f5f9;font-family:Arial,sans-serif;\">",
        "<div style=\"max-width:620px;margin:auto;background:#ffffff;padding:28px;border-radius:8px;\">",
        "<h1 style=\"font-size:23px;color:#253858;\">Pesquisa de satisfação</h1>",
        "<p>Olá, {{name}}!</p>",
        "<p>Você recebeu um convite para responder à pesquisa:</p>",
        "<h2 style=\"font-size:19px;color:#2763a4;\">{{title}}</h2>",
        "<p style=\"white-space:pre-wrap;\">{{description}}</p>",
        "<p><strong>Qual nota você daria, de 0 a 10?</strong></p>",
        "<p>0 representa a menor avaliação e 10 a maior.</p>",
        "<div style=\"margin:24px 0;\">",
        "{{#each options}}",
        "<a href=\"{{url}}\" style=\"display:inline-block;margin:4px;padding:12px 15px;background:#2463a0;color:#ffffff;text-decoration:none;border-radius:5px;font-weight:bold;\">{{value}}</a>",
        "{{/each}}",
        "</div>",
        "<p style=\"font-size:12px;color:#68778a;\">Cada convite aceita uma resposta. Os links são individuais.</p>",
        "</div>",
        "</body>",
        "</html>"
    ].join("\n")
);

function getEmailMode(): string {
    return (
        process.env.EMAIL_MODE || "json"
    ).trim().toLowerCase();
}

function createMailTransport() {
    const mode = getEmailMode();

    if (mode === "json") {
        return nodemailer.createTransport({
            jsonTransport: true
        });
    }

    if (mode !== "smtp") {
        throw new Error(
            "EMAIL_MODE deve ser json ou smtp."
        );
    }

    const port = Number(
        process.env.EMAIL_PORT || "1025"
    );

    if (
        !Number.isInteger(port) ||
        port < 1 ||
        port > 65535
    ) {
        throw new Error(
            "EMAIL_PORT deve ser uma porta válida."
        );
    }

    const username = process.env.EMAIL_USER || "";
    const password = process.env.EMAIL_PASS || "";

    const authentication = username && password
        ? {
            user: username,
            pass: password
        }
        : undefined;

    return nodemailer.createTransport({
        host: process.env.EMAIL_HOST || "127.0.0.1",
        port: port,
        secure: process.env.EMAIL_SECURE === "true",
        auth: authentication
    });
}

export async function sendSurveyInvitation(
    user: User,
    survey: Survey,
    invitation: Invitation
): Promise<MailDelivery> {

    const baseUrl = (
        process.env.PUBLIC_BASE_URL ||
        "http://localhost:3333"
    ).replace(/\/+$/, "");

    const options: RatingOption[] = [];

    for (let value = 0; value <= 10; value++) {
        options.push({
            value: value,
            url:
                baseUrl +
                "/answers/" +
                value +
                "?u=" +
                encodeURIComponent(invitation.id)
        });
    }

    const html = invitationTemplate({
        name: user.name,
        title: survey.title,
        description: survey.description,
        options: options
    });

    const transport = createMailTransport();

    const result = await transport.sendMail({
        from:
            process.env.EMAIL_FROM ||
            "Pesquisas Locais <no-reply@example.invalid>",

        to: user.email,

        subject:
            "Convite para pesquisa: " +
            survey.title,

        html: html
    });

    const mode = getEmailMode();

    return {
        mode: mode,

        preview: mode === "json"
            ? String((result as any).message)
            : null
    };
}