using System;
using System.Linq;
using SalesManagement.Models;

namespace SalesManagement.Data
{
    public static class DbInitializer
    {
        public static void Initialize(
            AppDbContext context)
        {
            if (context.Departments.Any() ||
                context.Sellers.Any() ||
                context.SalesRecords.Any())
            {
                return;
            }

            Department operations = new Department
            {
                Name = "Operações"
            };

            Department customerService = new Department
            {
                Name = "Atendimento"
            };

            Department technology = new Department
            {
                Name = "Tecnologia"
            };

            context.Departments.AddRange(
                operations,
                customerService,
                technology
            );

            context.SaveChanges();

            Seller sellerOne = new Seller
            {
                Name = "Nara Ferreira",
                Email = "nara@example.invalid",
                BirthDate = new DateTime(1994, 6, 14),
                BaseSalary = 2850.00m,
                DepartmentId = operations.Id
            };

            Seller sellerTwo = new Seller
            {
                Name = "Caio Andrade",
                Email = "caio@example.invalid",
                BirthDate = new DateTime(1991, 11, 8),
                BaseSalary = 3100.00m,
                DepartmentId = customerService.Id
            };

            Seller sellerThree = new Seller
            {
                Name = "Lina Martins",
                Email = "lina@example.invalid",
                BirthDate = new DateTime(1997, 3, 22),
                BaseSalary = 3600.00m,
                DepartmentId = technology.Id
            };

            Seller sellerFour = new Seller
            {
                Name = "Davi Reis",
                Email = "davi@example.invalid",
                BirthDate = new DateTime(1993, 9, 5),
                BaseSalary = 2950.00m,
                DepartmentId = operations.Id
            };

            context.Sellers.AddRange(
                sellerOne,
                sellerTwo,
                sellerThree,
                sellerFour
            );

            context.SaveChanges();

            SalesRecord[] records =
            {
                new SalesRecord
                {
                    Date = new DateTime(2025, 8, 12),
                    Amount = 450.00m,
                    Status = SaleStatus.Billed,
                    SellerId = sellerOne.Id
                },

                new SalesRecord
                {
                    Date = new DateTime(2025, 11, 19),
                    Amount = 780.40m,
                    Status = SaleStatus.Billed,
                    SellerId = sellerTwo.Id
                },

                new SalesRecord
                {
                    Date = new DateTime(2026, 1, 15),
                    Amount = 1150.75m,
                    Status = SaleStatus.Billed,
                    SellerId = sellerOne.Id
                },

                new SalesRecord
                {
                    Date = new DateTime(2026, 3, 2),
                    Amount = 300.00m,
                    Status = SaleStatus.Pending,
                    SellerId = sellerTwo.Id
                },

                new SalesRecord
                {
                    Date = new DateTime(2026, 5, 20),
                    Amount = 2100.00m,
                    Status = SaleStatus.Billed,
                    SellerId = sellerThree.Id
                },

                new SalesRecord
                {
                    Date = new DateTime(2026, 7, 11),
                    Amount = 650.50m,
                    Status = SaleStatus.Billed,
                    SellerId = sellerFour.Id
                },

                new SalesRecord
                {
                    Date = new DateTime(2026, 9, 22),
                    Amount = 125.50m,
                    Status = SaleStatus.Canceled,
                    SellerId = sellerFour.Id
                },

                new SalesRecord
                {
                    Date = new DateTime(2026, 10, 10),
                    Amount = 890.00m,
                    Status = SaleStatus.Pending,
                    SellerId = sellerThree.Id
                }
            };

            context.SalesRecords.AddRange(records);

            context.SaveChanges();
        }
    }
}