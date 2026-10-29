using System;
using System.Collections.Generic;
using SalesManagement.Models;

namespace SalesManagement.ViewModels
{
    public class SalesSearchViewModel
    {
        public DateTime MinDate { get; set; }

        public DateTime MaxDate { get; set; }

        public List<SalesRecord> Records { get; set; }

        public decimal Total { get; set; }

        public SalesSearchViewModel()
        {
            Records = new List<SalesRecord>();
        }
    }

    public class DepartmentSalesGroup
    {
        public int DepartmentId { get; set; }

        public string DepartmentName { get; set; }

        public List<SalesRecord> Records { get; set; }

        public decimal Total { get; set; }

        public DepartmentSalesGroup()
        {
            Records = new List<SalesRecord>();
        }
    }

    public class GroupingSearchViewModel
    {
        public DateTime MinDate { get; set; }

        public DateTime MaxDate { get; set; }

        public List<DepartmentSalesGroup> Groups { get; set; }

        public decimal GrandTotal { get; set; }

        public GroupingSearchViewModel()
        {
            Groups = new List<DepartmentSalesGroup>();
        }
    }
}