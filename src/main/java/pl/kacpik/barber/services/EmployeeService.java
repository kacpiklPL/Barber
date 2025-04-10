package pl.kacpik.barber.services;

import pl.kacpik.barber.model.Employee;

import java.util.Optional;

public interface EmployeeService {

    Employee addEmployee(Employee employee);

    void removeEmployee(Employee employee);

    Optional<Employee> getEmployeeById(long id);

    Optional<Employee> getEmployeeByPesel(String pesel);

}
