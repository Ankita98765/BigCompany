package com.bigcompany;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyAnalyzer {

    private static final double MIN_RATIO = 1.20;
    private static final double MAX_RATIO = 1.50;
    private static final int MAX_MANAGERS = 4;

    private final Map<Long, Employee> employees = new HashMap<>();
    private final Map<Long, List<Employee>> directReports = new HashMap<>();

    public CompanyAnalyzer(List<Employee> list) {
        for (Employee e : list) {
            employees.put(e.getId(), e);
            if (e.getManagerId() != null) {
                directReports.putIfAbsent(e.getManagerId(), new ArrayList<>());
                directReports.get(e.getManagerId()).add(e);
            }
        }
    }

    public List<SalaryFinding> findUnderpaidManagers() {
        List<SalaryFinding> result = new ArrayList<>();
        for (Map.Entry<Long, List<Employee>> entry : directReports.entrySet()) {
            Employee manager = employees.get(entry.getKey());
            if (manager == null) continue;
            double avg = average(entry.getValue());
            double min = avg * MIN_RATIO;
            if (manager.getSalary() < min) {
                result.add(new SalaryFinding(manager, avg, min - manager.getSalary()));
            }
        }
        return result;
    }

    public List<SalaryFinding> findOverpaidManagers() {
        List<SalaryFinding> result = new ArrayList<>();
        for (Map.Entry<Long, List<Employee>> entry : directReports.entrySet()) {
            Employee manager = employees.get(entry.getKey());
            if (manager == null) continue;
            double avg = average(entry.getValue());
            double max = avg * MAX_RATIO;
            if (manager.getSalary() > max) {
                result.add(new SalaryFinding(manager, avg, manager.getSalary() - max));
            }
        }
        return result;
    }

    public List<ReportingLineFinding> findLongReportingLines() {
        List<ReportingLineFinding> result = new ArrayList<>();
        for (Employee e : employees.values()) {
            // countManagers includes the CEO, so subtract 1 to get "between" count
            int between = countManagers(e.getId()) - 1;
            if (between > MAX_MANAGERS) {
                result.add(new ReportingLineFinding(e, between, between - MAX_MANAGERS));
            }
        }
        return result;
    }

    private int countManagers(long id) {
        Employee e = employees.get(id);
        if (e.getManagerId() == null) {
            return 0;
        }
        return 1 + countManagers(e.getManagerId());
    }

    private double average(List<Employee> list) {
        double total = 0;
        for (Employee e : list) {
            total += e.getSalary();
        }
        return total / list.size();
    }

    public static class SalaryFinding {
        public final Employee manager;
        public final double subordinatesAvg;
        public final double amount;

        public SalaryFinding(Employee manager, double subordinatesAvg, double amount) {
            this.manager = manager;
            this.subordinatesAvg = subordinatesAvg;
            this.amount = amount;
        }
    }

    public static class ReportingLineFinding {
        public final Employee employee;
        public final int managerCount;
        public final int excess;

        public ReportingLineFinding(Employee employee, int managerCount, int excess) {
            this.employee = employee;
            this.managerCount = managerCount;
            this.excess = excess;
        }
    }
}
