package pl.kacpik.barber.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.kacpik.barber.model.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Customer findByPhoneNumber(String phoneNumber);

}
