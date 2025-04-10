package pl.kacpik.barber.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kacpik.barber.model.CompanyService;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.repositories.CompanyServiceRepository;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
public class CompanyServiceServiceTest {

    @Autowired
    private CompanyServiceService companyServiceService;

    @Autowired
    private CompanyServiceRepository companyServiceRepository;

    @BeforeEach
    public void setUp(){
        companyServiceRepository.deleteAll();
    }

    @Test
    public void shouldCompanyServiceAddedToDatabase(){
        CompanyService companyService = CompanyService.builder()
                .name("Strzyżenie męskie")
                .duration(Duration.ofMinutes(40).toMillis())
                .price(BigDecimal.valueOf(80))
                .build();
        companyServiceService.addCompanyService(companyService);

        Iterable<CompanyService> result = companyServiceRepository.findAll();
        assertThat(result)
                .hasSize(1).
                containsExactly(companyService);
    }

}
