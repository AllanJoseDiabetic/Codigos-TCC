using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace SalesManagement.Models
{
    public class SalesRecord
    {
        public int Id { get; set; }

        [Required]
        [DataType(DataType.Date)]
        [Display(Name = "Data")]
        public DateTime Date { get; set; }

        [Range(
            typeof(decimal),
            "0.01",
            "999999999.99",
            ErrorMessage = "O valor deve ser maior que zero."
        )]
        [Column(TypeName = "decimal(18,2)")]
        [Display(Name = "Valor")]
        public decimal Amount { get; set; }

        [Display(Name = "Estado")]
        public SaleStatus Status { get; set; }

        [Required]
        public int SellerId { get; set; }

        public Seller Seller { get; set; }
    }
}