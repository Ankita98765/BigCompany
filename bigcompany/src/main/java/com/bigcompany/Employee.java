package com.bigcompany;

public class Employee {

    private final long id;
    private final String firstName;
    private final String lastName;
    private final double salary;
    private final Long managerId;

    public Employee(long id, String firstName, String lastName, double salary, Long managerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public double getSalary() {
        return salary;
    }

    public Long getManagerId() {
        return managerId;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
