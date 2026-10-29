import {
    Entity,
    PrimaryColumn,
    Column,
    CreateDateColumn,
    ManyToOne,
    JoinColumn
} from "typeorm";

import { User } from "./User";
import { Survey } from "./Survey";

@Entity("invitations")
export class Invitation {
    @PrimaryColumn({
        type: "varchar",
        length: 36
    })
    id: string;

    @ManyToOne(
        () => User,
        {
            nullable: false,
            onDelete: "CASCADE"
        }
    )
    @JoinColumn({
        name: "user_id"
    })
    user: User;

    @ManyToOne(
        () => Survey,
        {
            nullable: false,
            onDelete: "CASCADE"
        }
    )
    @JoinColumn({
        name: "survey_id"
    })
    survey: Survey;

    @Column({
        type: "integer",
        nullable: true
    })
    value: number | null;

    @CreateDateColumn({
        name: "sent_at",
        type: "datetime",
        default: () => "CURRENT_TIMESTAMP"
    })
    sentAt: Date;

    @Column({
        name: "answered_at",
        type: "datetime",
        nullable: true
    })
    answeredAt: Date | null;
}