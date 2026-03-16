package exercises.employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Manager extends FullTimeEmployee {
    private List<Employee> directReports = new ArrayList<>();
    private static final double MANAGEMENT_BONUS = (double) 15 / 100;

    public Manager(String name, String id, LocalDate hireDate) {
        super(name, id, hireDate);
    }

    public Manager(String name, String id, LocalDate hireDate, double annualSalary) {
        super(name, id, hireDate, annualSalary);
    }

    public Manager(String name, String id, LocalDate hireDate, double annualSalary, List<Employee> directReports) {
        super(name, id, hireDate, annualSalary);
        this.directReports = directReports;
    }

    public List<Employee> getDirectReports() {
        return List.copyOf(directReports);
    }

    public void setDirectReports(List<Employee> directReports) {
        this.directReports = directReports;
    }

    @Override
    public double calculateSalary(){
        return super.calculateSalary() * (1 + MANAGEMENT_BONUS);
    }
}
