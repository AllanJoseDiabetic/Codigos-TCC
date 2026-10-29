using Microsoft.AspNetCore.Mvc;

namespace SalesManagement.Controllers
{
    public class HomeController : Controller
    {
        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Error()
        {
            Response.StatusCode = 500;

            return View();
        }
    }
}