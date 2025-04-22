package pl.kacpik.barber.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.CustomerAccess;
import pl.kacpik.barber.repositories.CustomerAccessServiceRepository;
import pl.kacpik.barber.repositories.CustomerRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomerAccessServiceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerAccessService customerAccessService;

    @Autowired
    private CustomerAccessServiceRepository customerAccessServiceRepository;

    @BeforeEach
    public void setUp(){
        customerRepository.deleteAll();
        customerAccessServiceRepository.deleteAll();
    }

    private Customer createCustomer(){
        Customer customer = Customer.builder()
                .name("Kacper")
                .lastName("Test")
                .phoneNumber("123123123")
                .build();
        customerRepository.save(customer);
        return customer;
    }

    @Test
    public void shouldAddedCustomerAccessToDatabase(){
        Customer customer = createCustomer();

        CustomerAccess customerAccess = customerAccessService.getOrCreateCustomerAccess(customer);

        Iterable<CustomerAccess> result = customerAccessServiceRepository.findAll();
        assertThat(result)
                .hasSize(1).
                containsExactly(customerAccess);
    }

    @Test
    public void shouldAddedOnlyOneCustomerAccessForCustomer(){
        Customer customer = createCustomer();

        CustomerAccess customerAccess = customerAccessService.getOrCreateCustomerAccess(customer);
        CustomerAccess customerAccess2 = customerAccessService.getOrCreateCustomerAccess(customer);

        Iterable<CustomerAccess> result = customerAccessServiceRepository.findAll();
        assertThat(result)
                .hasSize(1).
                containsExactly(customerAccess);
        assertEquals(customerAccess, customerAccess2);
    }

    @Transactional
    @Test
    public void shouldRemoveCustomerAccessFromDatabaseByCustomerAccess(){
        Customer customer = createCustomer();
        CustomerAccess customerAccess = customerAccessService.getOrCreateCustomerAccess(customer);

        customerAccessService.removeCustomerAccess(customerAccess);

        Optional<CustomerAccess> optionalCustomerAccess = customerAccessServiceRepository.findById(customerAccess.getToken());
        assertTrue(optionalCustomerAccess.isEmpty());
    }

    @Transactional
    @Test
    public void shouldRemoveCustomerAccessFromDatabaseByToken(){
        Customer customer = createCustomer();
        CustomerAccess customerAccess = customerAccessService.getOrCreateCustomerAccess(customer);

        final String token = customerAccess.getToken();
        customerAccessService.removeCustomerAccess(token);

        Optional<CustomerAccess> optionalCustomerAccess = customerAccessServiceRepository.findById(token);
        assertTrue(optionalCustomerAccess.isEmpty());
    }



}