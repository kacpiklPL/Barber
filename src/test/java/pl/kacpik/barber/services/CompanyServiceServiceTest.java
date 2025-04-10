package pl.kacpik.barber.services;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kacpik.barber.repositories.CompanyServiceRepository;

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

}
