package pl.kacpik.barber.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.kacpik.barber.model.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

}
