using Microsoft.EntityFrameworkCore;
using SalesManagement.Models;

namespace SalesManagement.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(
            DbContextOptions<AppDbContext> options)
            : base(options)
        {
        }

        public DbSet<Department> Departments { get; set; }

        public DbSet<Seller> Sellers { get; set; }

        public DbSet<SalesRecord> SalesRecords { get; set; }

        protected override void OnModelCreating(
            ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<Department>(entity =>
            {
                entity.ToTable("Departments");

                entity.HasKey(department => department.Id);

                entity.Property(department => department.Name)
                    .IsRequired()
                    .HasMaxLength(80);
            });

            modelBuilder.Entity<Seller>(entity =>
            {
                entity.ToTable("Sellers");

                entity.HasKey(seller => seller.Id);

                entity.Property(seller => seller.Name)
                    .IsRequired()
                    .HasMaxLength(120);

                entity.Property(seller => seller.Email)
                    .IsRequired()
                    .HasMaxLength(150);

                entity.Property(seller => seller.BaseSalary)
                    .HasColumnType("decimal(18,2)");

                entity.HasOne(seller => seller.Department)
                    .WithMany(department => department.Sellers)
                    .HasForeignKey(seller => seller.DepartmentId)
                    .OnDelete(DeleteBehavior.Restrict);
            });

            modelBuilder.Entity<SalesRecord>(entity =>
            {
                entity.ToTable("SalesRecords");

                entity.HasKey(record => record.Id);

                entity.Property(record => record.Amount)
                    .HasColumnType("decimal(18,2)");

                entity.HasOne(record => record.Seller)
                    .WithMany(seller => seller.SalesRecords)
                    .HasForeignKey(record => record.SellerId)
                    .OnDelete(DeleteBehavior.Restrict);

                entity.HasIndex(record => record.Date);
            });
        }
    }
}