package pl.kacpik.barber.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.dto.CustomerDto;
import pl.kacpik.barber.repositories.CustomerRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;


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
    public void shouldAddedCustomerToDatabase(){
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
    public void shouldRemoveCustomerFromDatabase(){
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

        Optional<Customer> result = customerService.getCustomerById(savedCustomer.getId());
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

        Optional<Customer> result = customerService.getCustomerByPhoneNumber(phoneNumber);
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

        Optional<Customer> result = customerService.getCustomerByPhoneNumber("000000000");
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldUpdateCustomerSuccessful() {
        Customer customer = Customer.builder()
                .name("testName")
                .lastName("testLastName")
                .phoneNumber("123456789")
                .build();
        Customer savedCustomer = customerService.addCustomer(customer);
        CustomerDto customerDto = new CustomerDto(savedCustomer.getId(), "Klaudia", "Kowalska", "111111111");

        Customer resultCustomer = customerService.updateCustomer(savedCustomer.getId(), customerDto);

        assertNotNull(resultCustomer);
        assertEquals(customerDto.getName(), resultCustomer.getName());
        assertEquals(customerDto.getLastName(), resultCustomer.getLastName());
        assertEquals(customerDto.getPhoneNumber(), resultCustomer.getPhoneNumber());
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenCustomerNotFound() {
        long customerId = 1L;
        CustomerDto customerDto = new CustomerDto(customerId, "Klaudia", "Kowalska", "111111111");

        assertThrows(EntityNotFoundException.class, () -> customerService.updateCustomer(customerId, customerDto));
    }

}
