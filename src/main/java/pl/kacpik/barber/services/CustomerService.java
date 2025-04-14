package pl.kacpik.barber.services;

import pl.kacpik.barber.model.Customer;

import java.util.Optional;

public interface CustomerService {

    Customer addCustomer(Customer customer);

    void removeCustomer(Customer customer);

    Optional<Customer> getCustomerByPhoneNumber(String phoneNumber);

    Optional<Customer> getCustomerById(Long id);

}
