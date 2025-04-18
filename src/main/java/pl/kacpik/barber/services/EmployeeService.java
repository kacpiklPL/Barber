package pl.kacpik.barber.services;

import pl.kacpik.barber.model.Employee;
import pl.kacpik.barber.model.dto.EmployeeDto;

import java.util.Optional;

public interface EmployeeService {

    Employee addEmployee(Employee employee);

    void removeEmployee(Employee employee);

    Optional<Employee> getEmployeeById(long employeeId);

    Optional<Employee> getEmployeeByPesel(String pesel);

    Employee updateEmployee(long employeeId, EmployeeDto employeeDto);

}
