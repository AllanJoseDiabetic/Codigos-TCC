import "express-async-errors";

import express, {
    Request,
    Response,
    NextFunction
} from "express";

import * as yup from "yup";

import {
    getRepository
} from "typeorm";

import {
    v4 as uuidv4,
    validate as validateUuid
} from "uuid";

import { User } from "./entities/User";
import { Survey } from "./entities/Survey";
import { Invitation } from "./entities/Invitation";

import {
    sendSurveyInvitation
} from "./mail";

const app = express();

app.disable("x-powered-by");

app.use(
    express.json({
        limit: "32kb"
    })
);

const userSchema = yup.object().shape({
    name: yup.string()
        .trim()
        .min(2)
        .max(120)
        .required(),

    email: yup.string()
        .trim()
        .email()
        .max(254)
        .required()
}).noUnknown(true);

const surveySchema = yup.object().shape({
    title: yup.string()
        .trim()
        .min(1)
        .max(180)
        .required(),

    description: yup.string()
        .trim()
        .min(1)
        .required()
}).noUnknown(true);

const mailSchema = yup.object().shape({
    email: yup.string()
        .trim()
        .email()
        .required(),

    survey_id: yup.string()
        .required()
}).noUnknown(true);

async function validateBody(
    schema: yup.ObjectSchema<any>,
    req: Request,
    res: Response
): Promise<any | null> {

    try {
        return await schema.validate(
            req.body,
            {
                abortEarly: false
            }
        );
    } catch (error) {
        if (error instanceof yup.ValidationError) {
            res.status(400).json({
                error: "Dados inválidos.",
                details: error.errors
            });

            return null;
        }

        throw error;
    }
}

function invalidIdentifier(
    res: Response,
    field: string
): Response {
    return res.status(400).json({
        error:
            "Identificador inválido: " +
            field + "."
    });
}

app.post(
    "/users",
    async (req: Request, res: Response) => {

        const body = await validateBody(
            userSchema,
            req,
            res
        );

        if (body === null) {
            return;
        }

        const email = body.email
            .trim()
            .toLowerCase();

        const userRepository = getRepository(User);

        const existingUser = await userRepository.findOne({
            where: {
                email: email
            }
        });

        if (existingUser) {
            return res.status(400).json({
                error: "Este e-mail já está cadastrado."
            });
        }

        const user = userRepository.create({
            id: uuidv4(),
            name: body.name,
            email: email
        });

        try {
            await userRepository.save(user);
        } catch (error) {
            if (
                error.code === "SQLITE_CONSTRAINT" ||
                error.code === 19
            ) {
                return res.status(400).json({
                    error:
                        "Este e-mail já está cadastrado."
                });
            }

            throw error;
        }

        return res.status(201).json({
            id: user.id,
            name: user.name,
            email: user.email,
            createdAt: user.createdAt
        });
    }
);

app.post(
    "/surveys",
    async (req: Request, res: Response) => {

        const body = await validateBody(
            surveySchema,
            req,
            res
        );

        if (body === null) {
            return;
        }

        const surveyRepository = getRepository(Survey);

        const survey = surveyRepository.create({
            id: uuidv4(),
            title: body.title,
            description: body.description
        });

        await surveyRepository.save(survey);

        return res.status(201).json({
            id: survey.id,
            title: survey.title,
            description: survey.description,
            createdAt: survey.createdAt
        });
    }
);

app.get(
    "/surveys",
    async (req: Request, res: Response) => {

        const surveys = await getRepository(
            Survey
        ).find({
            order: {
                createdAt: "DESC"
            }
        });

        return res.status(200).json(
            surveys.map(function (survey) {
                return {
                    id: survey.id,
                    title: survey.title,
                    description: survey.description,
                    createdAt: survey.createdAt
                };
            })
        );
    }
);

app.post(
    "/sendMail",
    async (req: Request, res: Response) => {

        const body = await validateBody(
            mailSchema,
            req,
            res
        );

        if (body === null) {
            return;
        }

        if (!validateUuid(body.survey_id)) {
            return invalidIdentifier(
                res,
                "survey_id"
            );
        }

        const email = body.email
            .trim()
            .toLowerCase();

        const userRepository = getRepository(User);
        const surveyRepository = getRepository(Survey);
        const invitationRepository = getRepository(
            Invitation
        );

        const user = await userRepository.findOne({
            where: {
                email: email
            }
        });

        if (!user) {
            return res.status(404).json({
                error:
                    "Não existe usuário cadastrado " +
                    "com este e-mail."
            });
        }

        const survey = await surveyRepository.findOne(
            body.survey_id
        );

        if (!survey) {
            return res.status(404).json({
                error: "Pesquisa não encontrada."
            });
        }

        const invitation = invitationRepository.create({
            id: uuidv4(),
            user: user,
            survey: survey,
            value: null,
            answeredAt: null
        });

        await invitationRepository.save(
            invitation
        );

        let delivery;

        try {
            delivery = await sendSurveyInvitation(
                user,
                survey,
                invitation
            );
        } catch (error) {
            await invitationRepository.delete(
                invitation.id
            );

            console.error(
                "Falha na preparação ou envio do convite:",
                error.message
            );

            return res.status(502).json({
                error:
                    "Não foi possível enviar " +
                    "o convite por e-mail."
            });
        }

        const responseBody: any = {
            id: invitation.id,
            user_id: user.id,
            survey_id: survey.id,
            status: "pending",
            emailMode: delivery.mode
        };

        if (delivery.preview !== null) {
            responseBody.developmentPreview =
                delivery.preview;
        }

        return res.status(201).json(
            responseBody
        );
    }
);

app.get(
    "/answers/:value",
    async (req: Request, res: Response) => {

        const valueText = req.params.value;

        if (!/^(10|[0-9])$/.test(valueText)) {
            return res.status(400).json({
                error:
                    "A nota deve ser um inteiro " +
                    "entre 0 e 10."
            });
        }

        const invitationId = req.query.u;

        if (
            typeof invitationId !== "string" ||
            !validateUuid(invitationId)
        ) {
            return invalidIdentifier(
                res,
                "u"
            );
        }

        const invitationRepository = getRepository(
            Invitation
        );

        const invitation = await invitationRepository.findOne(
            invitationId
        );

        if (!invitation) {
            return res.status(404).json({
                error: "Convite não encontrado."
            });
        }

        if (invitation.value !== null) {
            return res.status(400).json({
                error:
                    "Este convite já foi respondido.",
                status: "answered"
            });
        }

        const value = Number(valueText);
        const answeredAt = new Date();

        const update = await invitationRepository
            .createQueryBuilder()
            .update(Invitation)
            .set({
                value: value,
                answeredAt: answeredAt
            })
            .where(
                "id = :id",
                {
                    id: invitationId
                }
            )
            .andWhere(
                "value IS NULL"
            )
            .execute();

        if (!update.affected) {
            return res.status(400).json({
                error:
                    "O convite já foi respondido " +
                    "por outra solicitação.",
                status: "answered"
            });
        }

        return res.status(200).json({
            message:
                "Resposta registrada com sucesso.",
            invitation_id: invitationId,
            value: value,
            status: "answered",
            answeredAt: answeredAt
        });
    }
);

app.get(
    "/nps/:survey_id",
    async (req: Request, res: Response) => {

        const surveyId = req.params.survey_id;

        if (!validateUuid(surveyId)) {
            return invalidIdentifier(
                res,
                "survey_id"
            );
        }

        const survey = await getRepository(
            Survey
        ).findOne(surveyId);

        if (!survey) {
            return res.status(404).json({
                error: "Pesquisa não encontrada."
            });
        }

        const responses = await getRepository(
            Invitation
        )
            .createQueryBuilder("invitation")
            .where(
                "invitation.survey_id = :surveyId",
                {
                    surveyId: surveyId
                }
            )
            .andWhere(
                "invitation.value IS NOT NULL"
            )
            .getMany();

        let detractors = 0;
        let passives = 0;
        let promoters = 0;

        for (
            let index = 0;
            index < responses.length;
            index++
        ) {
            const score = responses[index].value;

            if (score >= 0 && score <= 6) {
                detractors++;
            } else if (
                score >= 7 &&
                score <= 8
            ) {
                passives++;
            } else if (
                score >= 9 &&
                score <= 10
            ) {
                promoters++;
            }
        }

        const totalAnswers = responses.length;

        let nps = 0;

        if (totalAnswers > 0) {
            const calculated =
                100 *
                (promoters - detractors) /
                totalAnswers;

            nps = Number(
                calculated.toFixed(2)
            );
        }

        return res.status(200).json({
            survey_id: survey.id,
            survey_title: survey.title,

            detractors: detractors,
            passives: passives,
            promoters: promoters,

            totalAnswers: totalAnswers,
            nps: nps
        });
    }
);

app.use(
    (
        req: Request,
        res: Response
    ) => {
        return res.status(404).json({
            error: "Endpoint não encontrado."
        });
    }
);

app.use(
    (
        error: any,
        req: Request,
        res: Response,
        next: NextFunction
    ) => {

        if (
            error instanceof SyntaxError &&
            "body" in error
        ) {
            return res.status(400).json({
                error: "JSON inválido."
            });
        }

        console.error(
            "Erro interno:",
            error.message
        );

        return res.status(500).json({
            error:
                "Ocorreu um erro interno " +
                "ao processar a solicitação."
        });
    }
);

export default app;