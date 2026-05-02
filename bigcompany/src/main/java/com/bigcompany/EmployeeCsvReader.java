package com.bigcompany;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EmployeeCsvReader {

    public static List<Employee> read(Path file) throws IOException {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    employees.add(parse(line));
                }
            }
        }
        return employees;
    }

    static Employee parse(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 4 || parts.length > 5) {
            throw new IllegalArgumentException("Invalid CSV line: " + line);
        }
        try {
            long id = Long.parseLong(parts[0].trim());
            String firstName = parts[1].trim();
            String lastName = parts[2].trim();
            double salary = Double.parseDouble(parts[3].trim());
            Long managerId = null;
            if (parts.length == 5 && !parts[4].trim().isEmpty()) {
                managerId = Long.parseLong(parts[4].trim());
            }
            return new Employee(id, firstName, lastName, salary, managerId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bad numeric value in line: " + line, e);
        }
    }
}
