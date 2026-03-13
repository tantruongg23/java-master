package exercises.employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Department — models a company department using composition (has-a list of employees).
 *
 * <p>This class deliberately uses composition rather than inheritance to
 * demonstrate the "favor composition over inheritance" principle.
 */
public class Department {

    private final String name;
    private final List<Employee> employees;

    public Department(String name) {
        this.name = Objects.requireNonNull(name, "department name must not be null");
        this.employees = new ArrayList<>();
    }

    /**
     * Add an employee to this department.
     *
     * @param employee the employee to add
     * @throws IllegalArgumentException if the employee is already in the department
     */
    public void addEmployee(Employee employee) {
        // TODO: Check for duplicates (use equals/contains), then add.
        //       Throw IllegalArgumentException if already present.

        throw new UnsupportedOperationException("TODO: implement addEmployee()");
    }

    /**
     * Remove an employee from this department.
     *
     * @param employeeId the id of the employee to remove
     * @return {@code true} if the employee was found and removed
     */
    public boolean removeEmployee(String employeeId) {
        // TODO: Find the employee by id and remove them.
        //       Return true if removed, false if not found.

        throw new UnsupportedOperationException("TODO: implement removeEmployee()");
    }

    /**
     * Calculate the total payroll cost for the department.
     *
     * @return sum of all employee salaries
     */
    public double getTotalPayroll() {
        // TODO: Iterate over employees, sum calculateSalary().
        //       Demonstrate polymorphism — each employee type computes its own salary.

        throw new UnsupportedOperationException("TODO: implement getTotalPayroll()");
    }

    /**
     * Get all employees of a specific type.
     *
     * <p>Example: {@code getEmployeesByType(Contractor.class)}
     *
     * @param type the class of the employee type to filter by
     * @param <T>  the employee subtype
     * @return a list of employees matching the given type
     */
    public <T extends Employee> List<T> getEmployeesByType(Class<T> type) {
        // TODO: Filter the employee list by instanceof check and cast.
        //       Return a new list (defensive copy).

        throw new UnsupportedOperationException("TODO: implement getEmployeesByType()");
    }

    /**
     * @return an unmodifiable view of the employees in this department
     */
    public List<Employee> getEmployees() {
        return List.copyOf(employees);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Department{" +
                "name='" + name + '\'' +
                ", size=" + employees.size() +
                '}';
    }
}
