package pl.kacpik.barber.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kacpik.barber.model.CompanyService;
import pl.kacpik.barber.model.Employee;
import pl.kacpik.barber.repositories.CompanyServiceRepository;
import pl.kacpik.barber.repositories.EmployeeRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CompanyServiceServiceTest {

    @Autowired
    private CompanyServiceService companyServiceService;

    @Autowired
    private CompanyServiceRepository companyServiceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setUp(){
        companyServiceRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    private CompanyService createCompanyServiceWithEmployee(){
        Employee employee = Employee.builder()
                .pesel("00000000000")
                .name("testName")
                .lastName("testLastName")
                .birthday(LocalDate.of(2000,1, 27))
                .build();
        employeeRepository.save(employee);
        CompanyService companyService = CompanyService.builder()
                .name("Strzyżenie męskie")
                .duration(Duration.ofMinutes(40).toMillis())
                .price(BigDecimal.valueOf(80, 2))
                .employee(employee)
                .build();
        return companyServiceService.addCompanyService(companyService);
    }

    @Transactional
    @Test
    public void shouldCompanyServiceAddedToDatabase(){
        CompanyService companyService = createCompanyServiceWithEmployee();

        Iterable<CompanyService> result = companyServiceRepository.findAll();
        assertThat(result)
                .hasSize(1).
                containsExactly(companyService);
    }

    @Test
    public void shouldRemoveCompanyServiceFromDatabase(){
        CompanyService companyService = createCompanyServiceWithEmployee();

        companyServiceService.removeCompanyService(companyService);

        Optional<CompanyService> result = companyServiceRepository.findById(companyService.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldFindCompanyServiceById(){
        CompanyService companyService = createCompanyServiceWithEmployee();

        Optional<CompanyService> result = companyServiceRepository.findById(companyService.getId());
        assertTrue(result.isPresent());
        assertEquals(companyService, result.get());
    }

    @Test
    public void shouldReturnEmptyOptionalWhenCompanyServiceNotFoundById(){
        CompanyService companyService = createCompanyServiceWithEmployee();

        Optional<CompanyService> result = companyServiceRepository.findById(companyService.getId() + 1);
        assertTrue(result.isEmpty());
    }

    private Employee createEmployee(String pesel){
        Employee employee = Employee.builder()
                .pesel(pesel)
                .name("testName")
                .lastName("testLastName")
                .birthday(LocalDate.of(2000,1, 27))
                .build();
        employeeRepository.save(employee);
        return employee;
    }

    private CompanyService createCompanyService(Employee employee){
        CompanyService companyService = CompanyService.builder()
                .name("Strzyżenie męskie")
                .duration(Duration.ofMinutes(40).toMillis())
                .price(BigDecimal.valueOf(80, 2))
                .employee(employee)
                .build();
        return companyServiceService.addCompanyService(companyService);
    }

    @Test
    public void shouldReturnOnlyServicesAssignedToGivenEmployee(){
        Employee employee = createEmployee("00000000000");
        Employee employee2 = createEmployee("00000000001");
        CompanyService companyService = createCompanyService(employee);
        CompanyService companyService2 = createCompanyService(employee2);
        CompanyService companyService3 = createCompanyService(employee2);

        List<CompanyService> listEmployee1 = companyServiceService.getServicesByEmployeeId(employee.getId());
        List<CompanyService> listEmployee2 = companyServiceService.getServicesByEmployeeId(employee2.getId());

        assertThat(listEmployee1).hasSize(1);
        assertThat(listEmployee2).hasSize(2);
        assertThat(listEmployee1).containsOnly(companyService);
        assertThat(listEmployee2).containsOnly(companyService2, companyService3);

    }



}
