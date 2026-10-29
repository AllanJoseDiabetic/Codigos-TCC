import {
    Entity,
    PrimaryColumn,
    Column,
    CreateDateColumn
} from "typeorm";

@Entity("surveys")
export class Survey {
    @PrimaryColumn({
        type: "varchar",
        length: 36
    })
    id: string;

    @Column({
        type: "varchar",
        length: 180
    })
    title: string;

    @Column({
        type: "text"
    })
    description: string;

    @CreateDateColumn({
        name: "created_at",
        type: "datetime",
        default: () => "CURRENT_TIMESTAMP"
    })
    createdAt: Date;
}