package exercises.employee;

import java.time.LocalDate;

public class FullTimeEmployee extends Employee {
    private double annualSalary;

    public FullTimeEmployee(String name, String id, LocalDate hireDate) {
        super(name, id, hireDate);
    }

    public FullTimeEmployee(String name, String id, LocalDate hireDate, double annualSalary) {
        this(name, id, hireDate);
        this.annualSalary = annualSalary;
    }

    public double getAnnualSalary() {
        return annualSalary;
    }

    public void setAnnualSalary(double annualSalary) {
        this.annualSalary = annualSalary;
    }

    @Override
    public double calculateSalary() {
        return annualSalary / 12;
    }
}

