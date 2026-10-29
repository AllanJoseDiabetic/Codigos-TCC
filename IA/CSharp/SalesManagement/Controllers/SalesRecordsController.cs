using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SalesManagement.Data;
using SalesManagement.Models;
using SalesManagement.ViewModels;

namespace SalesManagement.Controllers
{
    public class SalesRecordsController : Controller
    {
        private readonly AppDbContext _context;

        public SalesRecordsController(AppDbContext context)
        {
            _context = context;
        }

        public IActionResult Index()
        {
            return View();
        }

        public async Task<IActionResult> SimpleSearch(
            DateTime? minDate,
            DateTime? maxDate)
        {
            DateTime today = DateTime.Today;

            DateTime startDate = minDate.HasValue
                ? minDate.Value.Date
                : new DateTime(today.Year, 1, 1);

            DateTime endDate = maxDate.HasValue
                ? maxDate.Value.Date
                : today;

            SalesSearchViewModel model =
                new SalesSearchViewModel
                {
                    MinDate = startDate,
                    MaxDate = endDate
                };

            if (startDate > endDate)
            {
                ModelState.AddModelError(
                    string.Empty,
                    "A data inicial não pode ser posterior " +
                    "à data final."
                );

                return View(model);
            }

            model.Records = await FindSales(
                startDate,
                endDate
            );

            model.Total = model.Records.Sum(
                record => record.Amount
            );

            return View(model);
        }

        public async Task<IActionResult> GroupingSearch(
            DateTime? minDate,
            DateTime? maxDate)
        {
            DateTime today = DateTime.Today;

            DateTime startDate = minDate.HasValue
                ? minDate.Value.Date
                : new DateTime(today.Year, 1, 1);

            DateTime endDate = maxDate.HasValue
                ? maxDate.Value.Date
                : today;

            GroupingSearchViewModel model =
                new GroupingSearchViewModel
                {
                    MinDate = startDate,
                    MaxDate = endDate
                };

            if (startDate > endDate)
            {
                ModelState.AddModelError(
                    string.Empty,
                    "A data inicial não pode ser posterior " +
                    "à data final."
                );

                return View(model);
            }

            List<SalesRecord> records = await FindSales(
                startDate,
                endDate
            );

            model.Groups = records
                .GroupBy(record => new
                {
                    record.Seller.DepartmentId,
                    record.Seller.Department.Name
                })
                .Select(group => new DepartmentSalesGroup
                {
                    DepartmentId = group.Key.DepartmentId,
                    DepartmentName = group.Key.Name,
                    Records = group
                        .OrderByDescending(record => record.Date)
                        .ThenBy(record => record.Id)
                        .ToList(),
                    Total = group.Sum(record => record.Amount)
                })
                .OrderBy(group => group.DepartmentName)
                .ToList();

            model.GrandTotal = records.Sum(
                record => record.Amount
            );

            return View(model);
        }

        private async Task<List<SalesRecord>> FindSales(
            DateTime startDate,
            DateTime endDate)
        {
            bool maximumDate =
                endDate == DateTime.MaxValue.Date;

            DateTime exclusiveEndDate = maximumDate
                ? DateTime.MaxValue
                : endDate.AddDays(1);

            return await _context.SalesRecords
                .Include(record => record.Seller)
                .ThenInclude(seller => seller.Department)
                .AsNoTracking()
                .Where(record =>
                    record.Date >= startDate
                    && (
                        maximumDate
                            ? record.Date <= exclusiveEndDate
                            : record.Date < exclusiveEndDate
                    )
                )
                .OrderByDescending(record => record.Date)
                .ThenBy(record => record.Id)
                .ToListAsync();
        }
    }
}