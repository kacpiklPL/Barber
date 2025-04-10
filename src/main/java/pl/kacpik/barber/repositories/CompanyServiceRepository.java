package pl.kacpik.barber.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.kacpik.barber.model.CompanyService;

@Repository
public interface CompanyServiceRepository extends CrudRepository<CompanyService, Long> {

}
