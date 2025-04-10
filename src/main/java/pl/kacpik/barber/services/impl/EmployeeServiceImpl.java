package pl.kacpik.barber.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kacpik.barber.model.Employee;
import pl.kacpik.barber.repositories.EmployeeRepository;
import pl.kacpik.barber.services.EmployeeService;

import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public void removeEmployee(Employee employee) {
        employeeRepository.delete(employee);
    }

    @Override
    public Optional<Employee> getEmployeeById(long id) {
        return employeeRepository.findById(id);
    }
}
