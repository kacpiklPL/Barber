package pl.kacpik.barber.services;

import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.CustomerAccess;

import java.util.Optional;

public interface CustomerAccessService {

    Optional<CustomerAccess> getCustomerAccess(String token);

    CustomerAccess getOrCreateCustomerAccess(Customer customer);

    void removeCustomerAccess(CustomerAccess customerAccess);

    void removeCustomerAccess(String token);

}
