package pl.kacpik.barber.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.kacpik.barber.model.CompanyService;
import pl.kacpik.barber.model.Employee;
import pl.kacpik.barber.services.CompanyServiceService;

import java.util.List;

@Repository
public interface CompanyServiceRepository extends CrudRepository<CompanyService, Long> {

    List<CompanyService> findAllByEmployeeId(long employeeId);

}
