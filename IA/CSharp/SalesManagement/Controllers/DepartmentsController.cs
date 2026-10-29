using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SalesManagement.Data;
using SalesManagement.Models;

namespace SalesManagement.Controllers
{
    public class DepartmentsController : Controller
    {
        private readonly AppDbContext _context;

        public DepartmentsController(AppDbContext context)
        {
            _context = context;
        }

        public async Task<IActionResult> Index()
        {
            var departments = await _context.Departments
                .AsNoTracking()
                .OrderBy(department => department.Name)
                .ToListAsync();

            return View(departments);
        }

        public async Task<IActionResult> Details(int? id)
        {
            if (!id.HasValue)
            {
                return BadRequest(
                    "O identificador do departamento é obrigatório."
                );
            }

            Department department = await _context.Departments
                .Include(item => item.Sellers)
                .AsNoTracking()
                .FirstOrDefaultAsync(
                    item => item.Id == id.Value
                );

            if (department == null)
            {
                return NotFound();
            }

            return View(department);
        }

        public IActionResult Create()
        {
            return View(new Department());
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Create(
            [Bind("Name")] Department department)
        {
            if (!ModelState.IsValid)
            {
                return View(department);
            }

            department.Name = department.Name.Trim();

            try
            {
                _context.Departments.Add(department);

                await _context.SaveChangesAsync();

                TempData["Success"] =
                    "Departamento cadastrado com sucesso.";

                return RedirectToAction(nameof(Index));
            }
            catch (DbUpdateException)
            {
                ModelState.AddModelError(
                    string.Empty,
                    "Não foi possível cadastrar o departamento."
                );

                Response.StatusCode = 409;

                return View(department);
            }
        }

        public async Task<IActionResult> Edit(int? id)
        {
            if (!id.HasValue)
            {
                return BadRequest(
                    "O identificador do departamento é obrigatório."
                );
            }

            Department department = await _context.Departments
                .FindAsync(id.Value);

            if (department == null)
            {
                return NotFound();
            }

            return View(department);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(
            int id,
            [Bind("Id,Name")] Department input)
        {
            if (id != input.Id)
            {
                return BadRequest(
                    "Os identificadores informados não correspondem."
                );
            }

            Department department = await _context.Departments
                .FindAsync(id);

            if (department == null)
            {
                return NotFound();
            }

            if (!ModelState.IsValid)
            {
                return View(input);
            }

            department.Name = input.Name.Trim();

            try
            {
                await _context.SaveChangesAsync();

                TempData["Success"] =
                    "Departamento atualizado com sucesso.";

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
                    "Não foi possível atualizar o departamento."
                );

                Response.StatusCode = 409;

                return View(input);
            }
        }

        public async Task<IActionResult> Delete(int? id)
        {
            if (!id.HasValue)
            {
                return BadRequest(
                    "O identificador do departamento é obrigatório."
                );
            }

            Department department = await _context.Departments
                .AsNoTracking()
                .FirstOrDefaultAsync(
                    item => item.Id == id.Value
                );

            if (department == null)
            {
                return NotFound();
            }

            return View(department);
        }

        [HttpPost]
        [ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> DeleteConfirmed(int id)
        {
            Department department = await _context.Departments
                .FindAsync(id);

            if (department == null)
            {
                return NotFound();
            }

            bool hasSellers = await _context.Sellers
                .AnyAsync(
                    seller => seller.DepartmentId == id
                );

            if (hasSellers)
            {
                ViewData["DeleteError"] =
                    "Este departamento possui vendedores associados " +
                    "e não pode ser excluído.";

                Response.StatusCode = 409;

                return View("Delete", department);
            }

            try
            {
                _context.Departments.Remove(department);

                await _context.SaveChangesAsync();

                TempData["Success"] =
                    "Departamento excluído com sucesso.";

                return RedirectToAction(nameof(Index));
            }
            catch (DbUpdateException)
            {
                ViewData["DeleteError"] =
                    "A exclusão não foi realizada porque existem " +
                    "registros relacionados ou uma restrição no banco.";

                Response.StatusCode = 409;

                return View("Delete", department);
            }
        }
    }
}