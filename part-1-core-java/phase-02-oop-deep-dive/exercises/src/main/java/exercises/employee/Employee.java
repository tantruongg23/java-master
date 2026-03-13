package exercises.employee;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstract base class for all employee types.
 *
 * <p>Subclasses must implement {@link #calculateSalary()} to define their
 * specific compensation logic.
 *
 * <p>Equality is based on the employee {@code id} field.
 *
 * <p>TODO: Create the following subclasses in separate files:
 * <ul>
 *   <li>{@code FullTimeEmployee} — has {@code annualSalary}; salary = annualSalary / 12</li>
 *   <li>{@code PartTimeEmployee} — has {@code hourlyRate}, {@code hoursWorked}; salary = rate * hours</li>
 *   <li>{@code Contractor} — has {@code dailyRate}, {@code daysWorked}; salary = rate * days</li>
 *   <li>{@code Manager extends FullTimeEmployee} — manages a team; salary includes a 15% bonus</li>
 * </ul>
 */
public abstract class Employee {

    private final String name;
    private final String id;
    private final LocalDate hireDate;

    protected Employee(String name, String id, LocalDate hireDate) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.hireDate = Objects.requireNonNull(hireDate, "hireDate must not be null");
    }

    /**
     * Calculate the employee's salary for the current period.
     *
     * @return the salary amount
     */
    public abstract double calculateSalary();

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", hireDate=" + hireDate +
                '}';
    }
}
