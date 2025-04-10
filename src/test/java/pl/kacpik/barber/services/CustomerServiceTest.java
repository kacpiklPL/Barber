package pl.kacpik.barber.services;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.repositories.CustomerRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.assertFalse;


@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp(){
        customerRepository.deleteAll();
    }

    @Test
    public void testShouldAddedCustomerToDatabase(){
        Customer customer = Customer.builder()
                .name("testName")
                .lastName("testLastName")
                .phoneNumber("123456789")
                .build();
        customerService.addCustomer(customer);

        Iterable<Customer> result = customerRepository.findAll();
        assertThat(result)
                .hasSize(1).
                containsExactly(customer);
    }

    @Test
    public void testShouldRemoveCustomerFromDatabase(){
        Customer customer = Customer.builder()
                .name("testName")
                .lastName("testLastName")
                .phoneNumber("123456789")
                .build();
        Customer savedCustomer = customerService.addCustomer(customer);

        customerService.removeCustomer(customer);
        assertTrue(customerRepository.findById(savedCustomer.getId()).isEmpty());
    }

    @Test
    public void shouldFindCustomerById(){
        Customer customer = Customer.builder()
                .name("testName")
                .lastName("testLastName")
                .phoneNumber("123456789")
                .build();
        Customer savedCustomer = customerService.addCustomer(customer);

        Optional<Customer> result = customerRepository.findById(savedCustomer.getId());
        assertTrue(result.isPresent());
    }

    @Test
    public void shouldReturnEmptyOptionalWhenCustomerNotFoundById(){
        Customer customer = Customer.builder()
                .name("testName")
                .lastName("testLastName")
                .phoneNumber("123456789")
                .build();
        Customer savedCustomer = customerService.addCustomer(customer);

        Optional<Customer> result = customerRepository.findById(savedCustomer.getId() + 1);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldFindCustomerByPhoneNumber(){
        final String phoneNumber = "123456789";
        Customer customer = Customer.builder()
                .name("testName")
                .lastName("testLastName")
                .phoneNumber(phoneNumber)
                .build();
        Customer savedCustomer = customerService.addCustomer(customer);

        Optional<Customer> result = customerService.getUserByPhoneNumber(phoneNumber);
        assertTrue(result.isPresent());
        assertEquals(savedCustomer, result.get());
    }

    @Test
    public void shouldReturnEmptyOptionalWhenCustomerNotFoundByPhoneNumber(){
        Customer customer = Customer.builder()
                .name("testName")
                .lastName("testLastName")
                .phoneNumber("123456789")
                .build();
        customerService.addCustomer(customer);

        Optional<Customer> result = customerService.getUserByPhoneNumber("000000000");
        assertTrue(result.isEmpty());
    }

}
