package com.bigcompany;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeCsvReaderTest {

    @Test
    void parsesCeoWithTrailingComma() {
        Employee e = EmployeeCsvReader.parse("123,Joe,Doe,60000,");
        assertEquals(123L, e.getId());
        assertEquals("Joe", e.getFirstName());
        assertEquals("Doe", e.getLastName());
        assertEquals(60000.0, e.getSalary());
        assertNull(e.getManagerId());
    }

    @Test
    void parsesCeoWithNoManagerColumn() {
        Employee e = EmployeeCsvReader.parse("123,Joe,Doe,60000");
        assertNull(e.getManagerId());
    }

    @Test
    void parsesEmployeeWithManager() {
        Employee e = EmployeeCsvReader.parse("305,Brett,Hardleaf,34000,300");
        assertEquals(305L, e.getId());
        assertEquals(Long.valueOf(300L), e.getManagerId());
    }

    @Test
    void rejectsInvalidLine() {
        assertThrows(Exception.class, () -> EmployeeCsvReader.parse("x,Joe,Doe,60000,"));
        assertThrows(Exception.class, () -> EmployeeCsvReader.parse("1,Joe,Doe"));
        assertThrows(Exception.class, () -> EmployeeCsvReader.parse("1,Joe,Doe,100,200,extra"));
    }

    @Test
    void readsFileSkippingHeader(@TempDir Path tmp) throws IOException {
        Path f = tmp.resolve("employees.csv");
        String csv = "Id,firstName,lastName,salary,managerId\n"
                + "123,Joe,Doe,60000,\n"
                + "\n"
                + "124,Martin,Chekov,45000,123\n";
        Files.write(f, csv.getBytes(StandardCharsets.UTF_8));

        List<Employee> list = EmployeeCsvReader.read(f);

        assertEquals(2, list.size());
        assertEquals(123L, list.get(0).getId());
        assertNull(list.get(0).getManagerId());
        assertEquals(124L, list.get(1).getId());
        assertEquals(Long.valueOf(123L), list.get(1).getManagerId());
    }
}
