package com.bigcompany;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompanyAnalyzerTest {

    private static Employee emp(long id, double salary, Long mgr) {
        return new Employee(id, "First" + id, "Last" + id, salary, mgr);
    }

    @Test
    void underpaidManagerIsDetected() {
        List<Employee> employees = Arrays.asList(
                emp(1, 150, null),
                emp(2, 110, 1L),
                emp(3, 100, 2L),
                emp(4, 100, 2L));

        CompanyAnalyzer analyzer = new CompanyAnalyzer(employees);
        List<CompanyAnalyzer.SalaryFinding> underpaid = analyzer.findUnderpaidManagers();

        assertEquals(1, underpaid.size());
        assertEquals(2L, underpaid.get(0).manager.getId());
        assertEquals(100.0, underpaid.get(0).subordinatesAvg, 1e-6);
        assertEquals(10.0, underpaid.get(0).amount, 1e-6);
        assertTrue(analyzer.findOverpaidManagers().isEmpty());
    }

    @Test
    void overpaidManagerIsDetected() {
        List<Employee> employees = Arrays.asList(
                emp(1, 270, null),
                emp(2, 200, 1L),
                emp(3, 100, 2L),
                emp(4, 100, 2L));

        CompanyAnalyzer analyzer = new CompanyAnalyzer(employees);
        List<CompanyAnalyzer.SalaryFinding> overpaid = analyzer.findOverpaidManagers();

        assertEquals(1, overpaid.size());
        assertEquals(2L, overpaid.get(0).manager.getId());
        assertEquals(50.0, overpaid.get(0).amount, 1e-6);
        assertTrue(analyzer.findUnderpaidManagers().isEmpty());
    }

    @Test
    void salaryAtBoundariesIsAccepted() {
        List<Employee> atMin = Arrays.asList(
                emp(1, 160, null),
                emp(2, 120, 1L),
                emp(3, 100, 2L),
                emp(4, 100, 2L));
        CompanyAnalyzer a1 = new CompanyAnalyzer(atMin);
        assertTrue(a1.findUnderpaidManagers().isEmpty());
        assertTrue(a1.findOverpaidManagers().isEmpty());

        List<Employee> atMax = Arrays.asList(
                emp(1, 200, null),
                emp(2, 150, 1L),
                emp(3, 100, 2L),
                emp(4, 100, 2L));
        CompanyAnalyzer a2 = new CompanyAnalyzer(atMax);
        assertTrue(a2.findUnderpaidManagers().isEmpty());
        assertTrue(a2.findOverpaidManagers().isEmpty());
    }

    @Test
    void ceoOnlyHasNoIssues() {
        CompanyAnalyzer analyzer = new CompanyAnalyzer(Collections.singletonList(emp(1, 100, null)));
        assertTrue(analyzer.findUnderpaidManagers().isEmpty());
        assertTrue(analyzer.findOverpaidManagers().isEmpty());
        assertTrue(analyzer.findLongReportingLines().isEmpty());
    }

    @Test
    void tooLongReportingLineIsDetected() {
        List<Employee> employees = Arrays.asList(
                emp(1, 1000, null),
                emp(2, 900, 1L),
                emp(3, 800, 2L),
                emp(4, 700, 3L),
                emp(5, 600, 4L),
                emp(6, 500, 5L),
                emp(7, 400, 6L),
                emp(8, 300, 7L));

        List<CompanyAnalyzer.ReportingLineFinding> lines = new CompanyAnalyzer(employees).findLongReportingLines();
        lines.sort((a, b) -> Long.compare(a.employee.getId(), b.employee.getId()));

        assertEquals(2, lines.size());
        assertEquals(7L, lines.get(0).employee.getId());
        assertEquals(5, lines.get(0).managerCount);
        assertEquals(1, lines.get(0).excess);
        assertEquals(8L, lines.get(1).employee.getId());
        assertEquals(6, lines.get(1).managerCount);
        assertEquals(2, lines.get(1).excess);
    }

    @Test
    void exampleFromRequirements() {
        List<Employee> employees = Arrays.asList(
                new Employee(123, "Joe", "Doe", 60000, null),
                new Employee(124, "Martin", "Chekov", 45000, 123L),
                new Employee(125, "Bob", "Ronstad", 47000, 123L),
                new Employee(300, "Alice", "Hasacat", 50000, 124L),
                new Employee(305, "Brett", "Hardleaf", 34000, 300L));

        CompanyAnalyzer analyzer = new CompanyAnalyzer(employees);
        List<CompanyAnalyzer.SalaryFinding> underpaid = analyzer.findUnderpaidManagers();

        assertEquals(1, underpaid.size());
        assertEquals(124L, underpaid.get(0).manager.getId());
        assertEquals(15000.0, underpaid.get(0).amount, 1e-6);
        assertTrue(analyzer.findOverpaidManagers().isEmpty());
        assertTrue(analyzer.findLongReportingLines().isEmpty());
    }
}
