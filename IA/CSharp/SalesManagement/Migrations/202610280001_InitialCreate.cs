using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Migrations;
using SalesManagement.Data;

namespace SalesManagement.Migrations
{
    [DbContext(typeof(AppDbContext))]
    [Migration("202610280001_InitialCreate")]
    public class InitialCreate : Migration
    {
        protected override void Up(
            MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Departments",
                columns: table => new
                {
                    Id = table.Column<int>(nullable: false)
                        .Annotation(
                            "MySql:ValueGenerationStrategy",
                            MySqlValueGenerationStrategy.IdentityColumn
                        ),

                    Name = table.Column<string>(
                        maxLength: 80,
                        nullable: false
                    )
                },
                constraints: table =>
                {
                    table.PrimaryKey(
                        "PK_Departments",
                        department => department.Id
                    );
                }
            );

            migrationBuilder.CreateTable(
                name: "Sellers",
                columns: table => new
                {
                    Id = table.Column<int>(nullable: false)
                        .Annotation(
                            "MySql:ValueGenerationStrategy",
                            MySqlValueGenerationStrategy.IdentityColumn
                        ),

                    Name = table.Column<string>(
                        maxLength: 120,
                        nullable: false
                    ),

                    Email = table.Column<string>(
                        maxLength: 150,
                        nullable: false
                    ),

                    BirthDate = table.Column<DateTime>(
                        nullable: false
                    ),

                    BaseSalary = table.Column<decimal>(
                        type: "decimal(18,2)",
                        nullable: false
                    ),

                    DepartmentId = table.Column<int>(
                        nullable: false
                    )
                },
                constraints: table =>
                {
                    table.PrimaryKey(
                        "PK_Sellers",
                        seller => seller.Id
                    );

                    table.ForeignKey(
                        name: "FK_Sellers_Departments_DepartmentId",
                        column: seller => seller.DepartmentId,
                        principalTable: "Departments",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict
                    );
                }
            );

            migrationBuilder.CreateTable(
                name: "SalesRecords",
                columns: table => new
                {
                    Id = table.Column<int>(nullable: false)
                        .Annotation(
                            "MySql:ValueGenerationStrategy",
                            MySqlValueGenerationStrategy.IdentityColumn
                        ),

                    Date = table.Column<DateTime>(
                        nullable: false
                    ),

                    Amount = table.Column<decimal>(
                        type: "decimal(18,2)",
                        nullable: false
                    ),

                    Status = table.Column<int>(
                        nullable: false
                    ),

                    SellerId = table.Column<int>(
                        nullable: false
                    )
                },
                constraints: table =>
                {
                    table.PrimaryKey(
                        "PK_SalesRecords",
                        record => record.Id
                    );

                    table.ForeignKey(
                        name: "FK_SalesRecords_Sellers_SellerId",
                        column: record => record.SellerId,
                        principalTable: "Sellers",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict
                    );
                }
            );

            migrationBuilder.CreateIndex(
                name: "IX_Sellers_DepartmentId",
                table: "Sellers",
                column: "DepartmentId"
            );

            migrationBuilder.CreateIndex(
                name: "IX_SalesRecords_Date",
                table: "SalesRecords",
                column: "Date"
            );

            migrationBuilder.CreateIndex(
                name: "IX_SalesRecords_SellerId",
                table: "SalesRecords",
                column: "SellerId"
            );
        }

        protected override void Down(
            MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "SalesRecords"
            );

            migrationBuilder.DropTable(
                name: "Sellers"
            );

            migrationBuilder.DropTable(
                name: "Departments"
            );
        }
    }
}