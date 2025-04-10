package pl.kacpik.barber.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.kacpik.barber.model.Employee;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    Optional<Employee> findByPesel(String pesel);

}
