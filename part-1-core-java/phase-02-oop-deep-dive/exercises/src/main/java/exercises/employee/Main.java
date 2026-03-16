package exercises.employee;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Employee> employees = List.of(
                // Full-time employee example
                new FullTimeEmployee("Alice Smith", "E001", LocalDate.of(2018, 6, 15), 72000),

                // Part-time employee example
                new PartTimeEmployee("Bob Johnson", "E002", LocalDate.of(2020, 3, 10), 25.0, 80),

                // Contractor example
                new Contractor("Charlie Brown", "C001", LocalDate.of(2022, 10, 1), 400.0, 20),

                // Manager example
                new Manager("Diana Prince", "M001", LocalDate.of(2015, 9, 20), 90000)
        );

        // Print each employee's information
        for (Employee employee : employees) {
            System.out.println(employee);
            System.out.println("Salary: " + employee.calculateSalary());
            System.out.println();
        }

        PayrollReport.generatePayrollReport(employees);
    }
}
