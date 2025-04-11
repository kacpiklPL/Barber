package pl.kacpik.barber.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kacpik.barber.model.CompanyService;
import pl.kacpik.barber.model.Employee;
import pl.kacpik.barber.repositories.CompanyServiceRepository;
import pl.kacpik.barber.services.CompanyServiceService;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceServiceImpl implements CompanyServiceService {

    @Autowired
    private CompanyServiceRepository companyServiceRepository;

    @Override
    public CompanyService addCompanyService(CompanyService companyService) {
        return companyServiceRepository.save(companyService);
    }

    @Override
    public void removeCompanyService(CompanyService companyService) {
        companyServiceRepository.delete(companyService);
    }

    @Override
    public Optional<CompanyService> getCompanyServiceById(long id) {
        return companyServiceRepository.findById(id);
    }

    @Override
    public List<CompanyService> getServicesByEmployeeId(long employeeId) {
        return companyServiceRepository.findAllByEmployeeId(employeeId);
    }

}
