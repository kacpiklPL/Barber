package pl.kacpik.barber.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kacpik.barber.repositories.CustomerAccessServiceRepository;
import pl.kacpik.barber.services.CustomerAccessService;

@Service
public class CustomerAccessServiceImpl implements CustomerAccessService {

    @Autowired
    private CustomerAccessServiceRepository customerAccessServiceRepository;

}
