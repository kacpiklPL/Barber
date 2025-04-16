package pl.kacpik.barber.services;

import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.dto.CustomerDto;

import java.util.Optional;

public interface CustomerService {

    Customer addCustomer(Customer customer);

    void removeCustomer(Customer customer);

    Optional<Customer> getCustomerByPhoneNumber(String phoneNumber);

    Optional<Customer> getCustomerById(Long id);

    Customer updateCustomer(long customerId, CustomerDto customerDto);

}
