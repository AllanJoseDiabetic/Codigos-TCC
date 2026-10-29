using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Metadata;
using SalesManagement.Data;

namespace SalesManagement.Migrations
{
    [DbContext(typeof(AppDbContext))]
    public class AppDbContextModelSnapshot : ModelSnapshot
    {
        protected override void BuildModel(
            ModelBuilder modelBuilder)
        {
            modelBuilder
                .HasAnnotation(
                    "ProductVersion",
                    "2.1.11-servicing-32099"
                );

            modelBuilder.Entity(
                "SalesManagement.Models.Department",
                entity =>
                {
                    entity.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasAnnotation(
                            "MySql:ValueGenerationStrategy",
                            MySqlValueGenerationStrategy.IdentityColumn
                        );

                    entity.Property<string>("Name")
                        .IsRequired()
                        .HasMaxLength(80);

                    entity.HasKey("Id");

                    entity.ToTable("Departments");
                }
            );

            modelBuilder.Entity(
                "SalesManagement.Models.Seller",
                entity =>
                {
                    entity.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasAnnotation(
                            "MySql:ValueGenerationStrategy",
                            MySqlValueGenerationStrategy.IdentityColumn
                        );

                    entity.Property<decimal>("BaseSalary")
                        .HasColumnType("decimal(18,2)");

                    entity.Property<DateTime>("BirthDate");

                    entity.Property<int>("DepartmentId");

                    entity.Property<string>("Email")
                        .IsRequired()
                        .HasMaxLength(150);

                    entity.Property<string>("Name")
                        .IsRequired()
                        .HasMaxLength(120);

                    entity.HasKey("Id");

                    entity.HasIndex("DepartmentId");

                    entity.ToTable("Sellers");
                }
            );

            modelBuilder.Entity(
                "SalesManagement.Models.SalesRecord",
                entity =>
                {
                    entity.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasAnnotation(
                            "MySql:ValueGenerationStrategy",
                            MySqlValueGenerationStrategy.IdentityColumn
                        );

                    entity.Property<decimal>("Amount")
                        .HasColumnType("decimal(18,2)");

                    entity.Property<DateTime>("Date");

                    entity.Property<int>("SellerId");

                    entity.Property<int>("Status");

                    entity.HasKey("Id");

                    entity.HasIndex("Date");

                    entity.HasIndex("SellerId");

                    entity.ToTable("SalesRecords");
                }
            );

            modelBuilder.Entity(
                "SalesManagement.Models.Seller",
                entity =>
                {
                    entity.HasOne(
                            "SalesManagement.Models.Department",
                            "Department"
                        )
                        .WithMany("Sellers")
                        .HasForeignKey("DepartmentId")
                        .OnDelete(DeleteBehavior.Restrict);
                }
            );

            modelBuilder.Entity(
                "SalesManagement.Models.SalesRecord",
                entity =>
                {
                    entity.HasOne(
                            "SalesManagement.Models.Seller",
                            "Seller"
                        )
                        .WithMany("SalesRecords")
                        .HasForeignKey("SellerId")
                        .OnDelete(DeleteBehavior.Restrict);
                }
            );
        }
    }
}