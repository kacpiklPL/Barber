package pl.kacpik.barber.services;

import pl.kacpik.barber.model.CompanyService;
import pl.kacpik.barber.model.Employee;

import java.util.List;
import java.util.Optional;

public interface CompanyServiceService {

    CompanyService addCompanyService(CompanyService companyService);

    void removeCompanyService(CompanyService companyService);

    Optional<CompanyService> getCompanyServiceById(long id);

    List<CompanyService> getServicesByEmployeeId(long employee);

}
