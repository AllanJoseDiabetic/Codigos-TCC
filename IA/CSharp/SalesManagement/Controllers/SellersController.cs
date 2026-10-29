
using System;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using SalesManagement.Data;
using SalesManagement.Models;

namespace SalesManagement.Controllers
{
    public class SellersController : Controller
    {
        private readonly AppDbContext _context;

        public SellersController(AppDbContext context)
        {
            _context = context;
        }

        public async Task<IActionResult> Index()
        {
            var sellers = await _context.Sellers
                .Include(seller => seller.Department)
                .AsNoTracking()
                .OrderBy(seller => seller.Name)
                .ToListAsync();

            return View(sellers);
        }

        public async Task<IActionResult> Details(int? id)
        {
            if (!id.HasValue)
            {
                return BadRequest(
                    "O identificador do vendedor é obrigatório."
                );
            }

            Seller seller = await _context.Sellers
                .Include(item => item.Department)
                .AsNoTracking()
                .FirstOrDefaultAsync(
                    item => item.Id == id.Value
                );

            if (seller == null)
            {
                return NotFound();
            }

            return View(seller);
        }

        public async Task<IActionResult> Create()
        {
            await LoadDepartments();

            return View(new Seller());
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Create(
            [Bind(
                "Name,Email,BirthDate,BaseSalary,DepartmentId"
            )] Seller seller)
        {
            await ValidateSeller(seller);

            if (!ModelState.IsValid)
            {
                await LoadDepartments(seller.DepartmentId);

                return View(seller);
            }

            seller.Name = seller.Name.Trim();
            seller.Email = seller.Email.Trim();

            try
            {
                _context.Sellers.Add(seller);

                await _context.SaveChangesAsync();

                TempData["Success"] =
                    "Vendedor cadastrado com sucesso.";

                return RedirectToAction(nameof(Index));
            }
            catch (DbUpdateException)
            {
                ModelState.AddModelError(
                    string.Empty,
                    "Não foi possível cadastrar o vendedor."
                );

                Response.StatusCode = 409;

                await LoadDepartments(seller.DepartmentId);

                return View(seller);
            }
        }

        public async Task<IActionResult> Edit(int? id)
        {
            if (!id.HasValue)
            {
                return BadRequest(
                    "O identificador do vendedor é obrigatório."
                );
            }

            Seller seller = await _context.Sellers
                .FindAsync(id.Value);

            if (seller == null)
            {
                return NotFound();
            }

            await LoadDepartments(seller.DepartmentId);

            return View(seller);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(
            int id,
            [Bind(
                "Id,Name,Email,BirthDate,BaseSalary,DepartmentId"
            )] Seller input)
        {
            if (id != input.Id)
            {
                return BadRequest(
                    "Os identificadores informados não correspondem."
                );
            }

            Seller seller = await _context.Sellers
                .FindAsync(id);

            if (seller == null)
            {
                return NotFound();
            }

            await ValidateSeller(input);

            if (!ModelState.IsValid)
            {
                await LoadDepartments(input.DepartmentId);

                return View(input);
            }

            seller.Name = input.Name.Trim();
            seller.Email = input.Email.Trim();
            seller.BirthDate = input.BirthDate;
            seller.BaseSalary = input.BaseSalary;
            seller.DepartmentId = input.DepartmentId;

            try
            {
                await _context.SaveChangesAsync();

                TempData["Success"] =
                    "Vendedor atualizado com sucesso.";

                return RedirectToAction(nameof(Index));
            }
            catch (DbUpdateConcurrencyException)
            {
                return NotFound();
            }
            catch (DbUpdateException)
            {
                ModelState.AddModelError(
                    string.Empty,
                    "Não foi possível atualizar o vendedor."
                );

                Response.StatusCode = 409;

                await LoadDepartments(input.DepartmentId);

                return View(input);
            }
        }

        public async Task<IActionResult> Delete(int? id)
        {
            if (!id.HasValue)
            {
                return BadRequest(
                    "O identificador do vendedor é obrigatório."
                );
            }

            Seller seller = await _context.Sellers
                .Include(item => item.Department)
                .AsNoTracking()
                .FirstOrDefaultAsync(
                    item => item.Id == id.Value
                );

            if (seller == null)
            {
                return NotFound();
            }

            return View(seller);
        }

        [HttpPost]
        [ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> DeleteConfirmed(int id)
        {
            Seller seller = await _context.Sellers
                .Include(item => item.Department)
                .FirstOrDefaultAsync(
                    item => item.Id == id
                );

            if (seller == null)
            {
                return NotFound();
            }

            bool hasSales = await _context.SalesRecords
                .AnyAsync(
                    record => record.SellerId == id
                );

            if (hasSales)
            {
                ViewData["DeleteError"] =
                    "Este vendedor possui registros de venda " +
                    "e não pode ser excluído.";

                Response.StatusCode = 409;

                return View("Delete", seller);
            }

            try
            {
                _context.Sellers.Remove(seller);

                await _context.SaveChangesAsync();

                TempData["Success"] =
                    "Vendedor excluído com sucesso.";

                return RedirectToAction(nameof(Index));
            }
            catch (DbUpdateException)
            {
                ViewData["DeleteError"] =
                    "O vendedor não pôde ser excluído devido " +
                    "a registros relacionados.";

                Response.StatusCode = 409;

                return View("Delete", seller);
            }
        }

        private async Task LoadDepartments(
            int? selectedDepartmentId = null)
        {
            var departments = await _context.Departments
                .AsNoTracking()
                .OrderBy(department => department.Name)
                .ToListAsync();

            ViewBag.Departments = new SelectList(
                departments,
                "Id",
                "Name",
                selectedDepartmentId
            );
        }

        private async Task ValidateSeller(Seller seller)
        {
            if (seller.BirthDate == default(DateTime))
            {
                ModelState.AddModelError(
                    "BirthDate",
                    "Informe uma data de nascimento válida."
                );
            }
            else if (seller.BirthDate.Date > DateTime.Today)
            {
                ModelState.AddModelError(
                    "BirthDate",
                    "A data de nascimento não pode ser futura."
                );
            }

            bool departmentExists = await _context.Departments
                .AnyAsync(
                    department => department.Id == seller.DepartmentId
                );

            if (!departmentExists)
            {
                ModelState.AddModelError(
                    "DepartmentId",
                    "Selecione um departamento existente."
                );
            }
        }
    }
}
