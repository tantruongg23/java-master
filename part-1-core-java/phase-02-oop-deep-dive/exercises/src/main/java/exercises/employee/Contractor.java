package exercises.employee;

import java.time.LocalDate;

public class Contractor extends Employee {
    private double dailyRate;
    private double daysWorked;

    protected Contractor(String name, String id, LocalDate hireDate) {
        super(name, id, hireDate);
    }

    public Contractor(String name, String id, LocalDate hireDate, double dailyRate, double daysWorked) {
        this(name, id, hireDate);
        this.dailyRate = dailyRate;
        this.daysWorked = daysWorked;
    }

    @Override
    public double calculateSalary() {
        return dailyRate * daysWorked;
    }

}
