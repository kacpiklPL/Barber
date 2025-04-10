package pl.kacpik.barber.services;

import pl.kacpik.barber.model.Employee;

public interface EmployeeService {

    Employee addEmployee(Employee employee);

    void removeEmployee(Employee employee);

}
