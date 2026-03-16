package exercises.employee;

import java.time.LocalDate;

public class PartTimeEmployee extends Employee {
    private double hourlyRate;
    private double hoursWorked;

    protected PartTimeEmployee(String name, String id, LocalDate hireDate) {
        super(name, id, hireDate);
    }

    public PartTimeEmployee(String name, String id, LocalDate hireDate, double hourlyRate, double hoursWorked) {
        this(name, id, hireDate);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    @Override
    public double calculateSalary() {
        return hourlyRate * hoursWorked;
    }
}
