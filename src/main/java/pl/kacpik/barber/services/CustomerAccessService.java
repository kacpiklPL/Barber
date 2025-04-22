package pl.kacpik.barber.services;

import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.CustomerAccess;

public interface CustomerAccessService {

    CustomerAccess getOrCreateCustomerAccess(Customer customer);

    void removeCustomerAccess(CustomerAccess customerAccess);

    void removeCustomerAccess(String token);

}
