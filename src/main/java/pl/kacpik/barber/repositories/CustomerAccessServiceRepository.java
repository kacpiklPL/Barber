package pl.kacpik.barber.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.kacpik.barber.model.CustomerAccess;

import java.util.Optional;

@Repository
public interface CustomerAccessServiceRepository extends CrudRepository<CustomerAccess, String> {

    Optional<CustomerAccess> findByCustomerId(long customerId);

}
