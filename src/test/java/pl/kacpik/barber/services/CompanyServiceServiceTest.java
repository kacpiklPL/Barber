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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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

}
