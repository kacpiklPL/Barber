package pl.kacpik.barber.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.CustomerAccess;
import pl.kacpik.barber.repositories.CustomerAccessServiceRepository;
import pl.kacpik.barber.services.CustomerAccessService;

import java.util.Optional;

@Service
public class CustomerAccessServiceImpl implements CustomerAccessService {

    private final CustomerAccessServiceRepository customerAccessServiceRepository;

    @Autowired
    public CustomerAccessServiceImpl(CustomerAccessServiceRepository customerAccessServiceRepository) {
        this.customerAccessServiceRepository = customerAccessServiceRepository;
    }

    @Override
    public Optional<CustomerAccess> getCustomerAccess(String token) {
        return customerAccessServiceRepository.findById(token);
    }

    @Override
    public CustomerAccess getOrCreateCustomerAccess(Customer customer) {
        Optional<CustomerAccess> customerAccess = customerAccessServiceRepository.findByCustomerId(customer.getId());
        return customerAccess.orElseGet(() -> createAndSaveNewCustomerAccess(customer));
    }

    @Override
    public void removeCustomerAccess(CustomerAccess customerAccess) {
        customerAccessServiceRepository.delete(customerAccess);
    }

    @Override
    public void removeCustomerAccess(String token) {
        Optional<CustomerAccess> customerAccess = customerAccessServiceRepository.findById(token);
        customerAccess.ifPresent(this::removeCustomerAccess);
    }

    private CustomerAccess createAndSaveNewCustomerAccess(Customer customer){
        CustomerAccess customerAccess = CustomerAccess.builder()
                .customer(customer)
                .build();
        customerAccessServiceRepository.save(customerAccess);
        return customerAccess;
    }

}
