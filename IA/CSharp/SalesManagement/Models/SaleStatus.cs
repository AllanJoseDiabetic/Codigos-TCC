using System.ComponentModel.DataAnnotations;

namespace SalesManagement.Models
{
    public enum SaleStatus
    {
        [Display(Name = "Pendente")]
        Pending = 0,

        [Display(Name = "Faturado")]
        Billed = 1,

        [Display(Name = "Cancelado")]
        Canceled = 2
    }
}