package pl.kacpik.barber.services;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.repositories.CustomerRepository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

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

}
