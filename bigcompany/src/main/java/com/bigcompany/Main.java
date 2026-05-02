package com.bigcompany;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar bigcompany.jar <employees.csv>");
            System.exit(1);
        }

        List<Employee> employees;
        try {
            employees = EmployeeCsvReader.read(Paths.get(args[0]));
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
            System.exit(1);
            return;
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid data: " + e.getMessage());
            System.exit(1);
            return;
        }

        CompanyAnalyzer analyzer = new CompanyAnalyzer(employees);

        System.out.println("Managers earning less than they should:");
        List<CompanyAnalyzer.SalaryFinding> underpaid = analyzer.findUnderpaidManagers();
        if (underpaid.isEmpty()) {
            System.out.println("  none");
        } else {
            for (CompanyAnalyzer.SalaryFinding f : underpaid) {
                System.out.printf("  %s earns %.2f less than required (subordinates avg: %.2f)%n",
                        f.manager.getFullName(), f.amount, f.subordinatesAvg);
            }
        }

        System.out.println();
        System.out.println("Managers earning more than they should:");
        List<CompanyAnalyzer.SalaryFinding> overpaid = analyzer.findOverpaidManagers();
        if (overpaid.isEmpty()) {
            System.out.println("  none");
        } else {
            for (CompanyAnalyzer.SalaryFinding f : overpaid) {
                System.out.printf("  %s earns %.2f more than allowed (subordinates avg: %.2f)%n",
                        f.manager.getFullName(), f.amount, f.subordinatesAvg);
            }
        }

        System.out.println();
        System.out.println("Employees with too long reporting line:");
        List<CompanyAnalyzer.ReportingLineFinding> longLines = analyzer.findLongReportingLines();
        if (longLines.isEmpty()) {
            System.out.println("  none");
        } else {
            for (CompanyAnalyzer.ReportingLineFinding f : longLines) {
                System.out.printf("  %s has %d managers (too long by %d)%n",
                        f.employee.getFullName(), f.managerCount, f.excess);
            }
        }
    }
}
