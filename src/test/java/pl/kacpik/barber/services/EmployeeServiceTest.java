package pl.kacpik.barber.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kacpik.barber.model.Employee;
import pl.kacpik.barber.repositories.EmployeeRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    public void shouldAddedEmployeeToDatabase(){
        Employee employee = Employee.builder()
                .pesel("00000000000")
                .name("testName")
                .lastName("testLastName")
                .birthday(LocalDate.of(2000,1, 27))
                .build();
        employeeService.addEmployee(employee);

        Iterable<Employee> result = employeeRepository.findAll();
        assertThat(result)
                .hasSize(1).
                containsExactly(employee);
    }

    @Test
    public void shouldRemoveEmployeeFromDatabase(){
        Employee employee = Employee.builder()
                .pesel("00000000000")
                .name("testName")
                .lastName("testLastName")
                .birthday(LocalDate.of(2000,1, 27))
                .build();
        Employee savedEmployee = employeeService.addEmployee(employee);

        employeeService.removeEmployee(savedEmployee);

        Optional<Employee> result = employeeRepository.findById(savedEmployee.getPesel());
        assertTrue(result.isEmpty());
    }
}
