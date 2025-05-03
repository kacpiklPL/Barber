package pl.kacpik.barber.services.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kacpik.barber.model.Employee;
import pl.kacpik.barber.model.dto.EmployeeDto;
import pl.kacpik.barber.repositories.EmployeeRepository;
import pl.kacpik.barber.services.EmployeeService;

import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public void removeEmployee(Employee employee) {
        employeeRepository.delete(employee);
    }

    @Override
    public Optional<Employee> getEmployeeById(long employeeId) {
        return employeeRepository.findById(employeeId);
    }

    @Override
    public Optional<Employee> getEmployeeByPesel(String pesel) {
        return employeeRepository.findByPesel(pesel);
    }

    @Override
    public Employee updateEmployee(long employeeId, EmployeeDto employeeDto) {
        Employee employee = getEmployeeById(employeeId)
                .orElseThrow(EntityNotFoundException::new);
        updateEmployeeFromDto(employee, employeeDto);
        return employee;
    }

    private void updateEmployeeFromDto(Employee employee, EmployeeDto employeeDto){
        employee.setName(employeeDto.getName());
        employee.setLastName(employeeDto.getLastName());
        employee.setPesel(employeeDto.getPesel());
        employee.setBirthday(employeeDto.getBirthday());
    }
}
