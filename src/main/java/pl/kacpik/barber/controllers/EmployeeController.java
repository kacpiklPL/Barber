package pl.kacpik.barber.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kacpik.barber.mappers.EmployeeMapperImpl;
import pl.kacpik.barber.model.Employee;
import pl.kacpik.barber.model.dto.EmployeeDto;
import pl.kacpik.barber.services.EmployeeService;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeMapperImpl employeeMapper;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping(path = "/employees")
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto){
        Employee employee = employeeMapper.mapFrom(employeeDto);
        Employee savedEmployee = employeeService.addEmployee(employee);
        return new ResponseEntity<>(employeeMapper.mapTo(savedEmployee), HttpStatus.CREATED);
    }

    @GetMapping(path = "/employees/{employeeId}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable("employeeId") Long employeeId){
        Employee employee = employeeService.getEmployeeById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + employeeId));
        return new ResponseEntity<>(employeeMapper.mapTo(employee), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/employees/{employeeId}")
    public ResponseEntity<Void> removeEmployee(@PathVariable("employeeId") Long employeeId){
        Employee employee = employeeService.getEmployeeById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + employeeId));
        employeeService.removeEmployee(employee);
        return ResponseEntity.noContent().build();
    }

}
