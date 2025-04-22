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

    @Autowired
    private CustomerAccessServiceRepository customerAccessServiceRepository;

    @Override
    public CustomerAccess getOrCreateCustomerAccess(Customer customer) {
        CustomerAccess customerAccess = customer.getCustomerAccess();
        if (customerAccess == null) {
            return createAndSaveNewCustomerAccess(customer);
        }
        return customerAccess;
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
                .token(customer.getId() + "")
                .customer(customer)
                .build();
        customerAccessServiceRepository.save(customerAccess);
        return customerAccess;
    }

}
