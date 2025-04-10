package pl.kacpik.barber.services.impl;

import org.springframework.stereotype.Service;
import pl.kacpik.barber.model.CompanyService;
import pl.kacpik.barber.repositories.CompanyServiceRepository;
import pl.kacpik.barber.services.CompanyServiceService;

@Service
public class CompanyServiceServiceImpl implements CompanyServiceService {

    private CompanyServiceRepository companyServiceRepository;

    @Override
    public CompanyService addCompanyService(CompanyService companyService) {
        return companyServiceRepository.save(companyService);
    }

}
