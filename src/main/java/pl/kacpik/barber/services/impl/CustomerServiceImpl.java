package pl.kacpik.barber.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.repositories.CustomerRepository;
import pl.kacpik.barber.services.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void removeCustomer(Customer customer) {
        customerRepository.delete(customer);
    }

}
