package pl.kacpik.barber.services.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kacpik.barber.exceptions.DuplicatePhoneNumberException;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.dto.CustomerDto;
import pl.kacpik.barber.repositories.CustomerRepository;
import pl.kacpik.barber.services.CustomerService;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer addCustomer(Customer customer) {
        if(existsByPhoneNumber(customer.getPhoneNumber())){
            throw new DuplicatePhoneNumberException("Phone number already in use: " + customer.getPhoneNumber());
        }
        return customerRepository.save(customer);
    }

    private boolean existsByPhoneNumber(String phoneNumber){
        return getCustomerByPhoneNumber(phoneNumber).isPresent();
    }

    @Override
    public void removeCustomer(Customer customer) {
        customerRepository.delete(customer);
    }

    @Override
    public Optional<Customer> getCustomerByPhoneNumber(String phoneNumber) {
        return Optional.ofNullable(customerRepository.findByPhoneNumber(phoneNumber));
    }

    @Override
    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    @Override
    public Customer updateCustomer(long customerId, CustomerDto customerDto) {
        Customer customer = getCustomerById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
        updateCustomerFromDto(customer, customerDto);
        return customer;
    }

    private void updateCustomerFromDto(Customer customer, CustomerDto customerDto){
        customer.setName(customerDto.getName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
    }

}
