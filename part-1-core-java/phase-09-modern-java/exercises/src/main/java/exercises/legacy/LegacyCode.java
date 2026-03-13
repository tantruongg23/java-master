package exercises.legacy;

import java.util.*;

/**
 * Legacy Java 8-style code — refactor this to use modern Java features.
 *
 * <p><b>Instructions:</b> modernise each section using the feature indicated in the
 * comments. Track before/after line counts. Do not change observable behaviour.</p>
 *
 * <p>Features to apply: {@code var}, pattern matching, records, text blocks,
 * switch expressions, sealed classes.</p>
 */
public class LegacyCode {

    // ─── 1. Data-holder classes → records ────────────────────────────

    /** TODO: Convert to a record. */
    public static class Employee {
        private final String name;
        private final String department;
        private final double salary;

        public Employee(String name, String department, double salary) {
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        public String getName() { return name; }
        public String getDepartment() { return department; }
        public double getSalary() { return salary; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Employee employee = (Employee) o;
            return Double.compare(employee.salary, salary) == 0 &&
                    Objects.equals(name, employee.name) &&
                    Objects.equals(department, employee.department);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, department, salary);
        }

        @Override
        public String toString() {
            return "Employee{name='" + name + "', department='" + department +
                    "', salary=" + salary + "}";
        }
    }

    /** TODO: Convert to a record. */
    public static class Address {
        private final String street;
        private final String city;
        private final String zipCode;
        private final String country;

        public Address(String street, String city, String zipCode, String country) {
            this.street = street;
            this.city = city;
            this.zipCode = zipCode;
            this.country = country;
        }

        public String getStreet() { return street; }
        public String getCity() { return city; }
        public String getZipCode() { return zipCode; }
        public String getCountry() { return country; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Address address = (Address) o;
            return Objects.equals(street, address.street) &&
                    Objects.equals(city, address.city) &&
                    Objects.equals(zipCode, address.zipCode) &&
                    Objects.equals(country, address.country);
        }

        @Override
        public int hashCode() {
            return Objects.hash(street, city, zipCode, country);
        }

        @Override
        public String toString() {
            return "Address{street='" + street + "', city='" + city +
                    "', zipCode='" + zipCode + "', country='" + country + "'}";
        }
    }

    // ─── 2. instanceof chains → pattern matching ─────────────────────

    /** TODO: Refactor to use pattern matching for instanceof. */
    public static String describeShape(Object shape) {
        if (shape instanceof String) {
            String s = (String) shape;
            return "Text with length " + s.length();
        } else if (shape instanceof Integer) {
            Integer i = (Integer) shape;
            return "Number: " + i;
        } else if (shape instanceof List) {
            List<?> list = (List<?>) shape;
            return "List with " + list.size() + " elements";
        } else if (shape instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) shape;
            return "Map with " + map.size() + " entries";
        } else if (shape instanceof double[]) {
            double[] arr = (double[]) shape;
            return "Double array of length " + arr.length;
        } else {
            return "Unknown type: " + shape.getClass().getSimpleName();
        }
    }

    // ─── 3. Old-style switch → switch expressions ────────────────────

    /** TODO: Refactor to a switch expression with arrow syntax. */
    public static String dayType(String day) {
        String type;
        switch (day.toUpperCase()) {
            case "MONDAY":
            case "TUESDAY":
            case "WEDNESDAY":
            case "THURSDAY":
            case "FRIDAY":
                type = "Weekday";
                break;
            case "SATURDAY":
            case "SUNDAY":
                type = "Weekend";
                break;
            default:
                type = "Unknown";
                break;
        }
        return type;
    }

    /** TODO: Refactor to a switch expression. */
    public static int priority(String level) {
        int p;
        switch (level) {
            case "CRITICAL":
                p = 1;
                break;
            case "HIGH":
                p = 2;
                break;
            case "MEDIUM":
                p = 3;
                break;
            case "LOW":
                p = 4;
                break;
            default:
                p = 5;
                break;
        }
        return p;
    }

    // ─── 4. String concatenation → text blocks ───────────────────────

    /** TODO: Refactor to a text block. */
    public static String sqlQuery() {
        return "SELECT e.name, e.department, e.salary\n" +
                "FROM employees e\n" +
                "JOIN departments d ON e.department_id = d.id\n" +
                "WHERE e.salary > 50000\n" +
                "  AND d.active = true\n" +
                "ORDER BY e.salary DESC\n" +
                "LIMIT 10;";
    }

    /** TODO: Refactor to a text block. */
    public static String jsonTemplate(String name, int age) {
        return "{\n" +
                "  \"name\": \"" + name + "\",\n" +
                "  \"age\": " + age + ",\n" +
                "  \"active\": true\n" +
                "}";
    }

    /** TODO: Refactor to a text block. */
    public static String htmlPage() {
        return "<html>\n" +
                "  <head>\n" +
                "    <title>Report</title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <h1>Monthly Report</h1>\n" +
                "    <p>This report was generated automatically.</p>\n" +
                "  </body>\n" +
                "</html>";
    }

    // ─── 5. Verbose local types → var ────────────────────────────────

    /** TODO: Replace verbose type declarations with var where appropriate. */
    public static void processEmployees(List<Employee> employees) {
        Map<String, List<Employee>> byDepartment = new HashMap<>();
        for (Employee employee : employees) {
            List<Employee> departmentList = byDepartment.computeIfAbsent(
                    employee.getDepartment(), k -> new ArrayList<>());
            departmentList.add(employee);
        }

        StringBuilder report = new StringBuilder();
        for (Map.Entry<String, List<Employee>> entry : byDepartment.entrySet()) {
            String department = entry.getKey();
            List<Employee> deptEmployees = entry.getValue();
            double totalSalary = 0.0;
            for (Employee emp : deptEmployees) {
                totalSalary += emp.getSalary();
            }
            double averageSalary = totalSalary / deptEmployees.size();
            String line = String.format("%s: %d employees, avg salary: %.2f%n",
                    department, deptEmployees.size(), averageSalary);
            report.append(line);
        }

        System.out.println(report);
    }
}
