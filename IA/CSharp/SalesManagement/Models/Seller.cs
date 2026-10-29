using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace SalesManagement.Models
{
    public class Seller
    {
        public int Id { get; set; }

        [Required(ErrorMessage = "Informe o nome do vendedor.")]
        [StringLength(
            120,
            MinimumLength = 2,
            ErrorMessage = "O nome deve conter entre 2 e 120 caracteres."
        )]
        [Display(Name = "Nome")]
        public string Name { get; set; }

        [Required(ErrorMessage = "Informe o e-mail.")]
        [EmailAddress(ErrorMessage = "Informe um e-mail válido.")]
        [StringLength(150)]
        [Display(Name = "E-mail")]
        public string Email { get; set; }

        [Required(ErrorMessage = "Informe a data de nascimento.")]
        [DataType(DataType.Date)]
        [Display(Name = "Data de nascimento")]
        public DateTime BirthDate { get; set; }

        [Required(ErrorMessage = "Informe o salário-base.")]
        [Range(
            typeof(decimal),
            "0.01",
            "999999999.99",
            ErrorMessage = "Informe um salário-base maior que zero."
        )]
        [Column(TypeName = "decimal(18,2)")]
        [Display(Name = "Salário-base")]
        public decimal BaseSalary { get; set; }

        [Range(
            1,
            int.MaxValue,
            ErrorMessage = "Selecione um departamento."
        )]
        [Display(Name = "Departamento")]
        public int DepartmentId { get; set; }

        public Department Department { get; set; }

        public ICollection<SalesRecord> SalesRecords { get; set; }

        public Seller()
        {
            SalesRecords = new List<SalesRecord>();
        }
    }
}