using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace SalesManagement.Models
{
    public class Department
    {
        public int Id { get; set; }

        [Required(ErrorMessage = "Informe o nome do departamento.")]
        [StringLength(
            80,
            MinimumLength = 2,
            ErrorMessage = "O nome deve conter entre 2 e 80 caracteres."
        )]
        [Display(Name = "Nome")]
        public string Name { get; set; }

        public ICollection<Seller> Sellers { get; set; }

        public Department()
        {
            Sellers = new List<Seller>();
        }
    }
}