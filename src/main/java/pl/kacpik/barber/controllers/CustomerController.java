package pl.kacpik.barber.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pl.kacpik.barber.services.CustomerService;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

}
