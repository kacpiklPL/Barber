package pl.kacpik.barber.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pl.kacpik.barber.mappers.EmployeeMapperImpl;
import pl.kacpik.barber.services.EmployeeService;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeMapperImpl employeeMapper;

    @Autowired
    private EmployeeService employeeService;

}
