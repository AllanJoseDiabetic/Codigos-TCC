import {
    Entity,
    PrimaryColumn,
    Column,
    CreateDateColumn
} from "typeorm";

@Entity("users")
export class User {
    @PrimaryColumn({
        type: "varchar",
        length: 36
    })
    id: string;

    @Column({
        type: "varchar",
        length: 120
    })
    name: string;

    @Column({
        type: "varchar",
        length: 254,
        unique: true
    })
    email: string;

    @CreateDateColumn({
        name: "created_at",
        type: "datetime",
        default: () => "CURRENT_TIMESTAMP"
    })
    createdAt: Date;
}