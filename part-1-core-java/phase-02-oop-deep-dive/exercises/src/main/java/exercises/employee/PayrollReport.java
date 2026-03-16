package exercises.employee;

import java.util.List;

public class PayrollReport {
    public static void generatePayrollReport(List<? extends Employee> employees) {
        System.out.println("Payroll Report");
        employees.forEach(e -> System.out.println(e.toString() + ". Salary = " + e.calculateSalary()));
    }
}
