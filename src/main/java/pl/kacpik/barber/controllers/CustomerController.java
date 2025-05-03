package pl.kacpik.barber.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kacpik.barber.mappers.CustomerMapperImpl;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.dto.CustomerDto;
import pl.kacpik.barber.services.CustomerService;

import java.util.Optional;

@RestController
public class CustomerController {

    private final CustomerMapperImpl customerMapper;

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerMapperImpl customerMapper, CustomerService customerService) {
        this.customerMapper = customerMapper;
        this.customerService = customerService;
    }

    private Customer getCustomerOrThrow(Long customerId){
        return customerService.getCustomerById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
    }

    @PostMapping(path = "/customers")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto customerDto){
        Customer customer = customerMapper.mapFrom(customerDto);
        Customer saveCustomer = customerService.addCustomer(customer);
        return new ResponseEntity<>(customerMapper.mapTo(saveCustomer), HttpStatus.CREATED);
    }

    @GetMapping(path = "/customers/{customerId}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable("customerId") Long customerId){
        Customer customer = getCustomerOrThrow(customerId);
        return new ResponseEntity<>(customerMapper.mapTo(customer), HttpStatus.OK);
    }

    @DeleteMapping(path = "/customers/{customerId}")
    public ResponseEntity<Void> removeCustomer(@PathVariable("customerId") Long customerId){
        Customer customer = getCustomerOrThrow(customerId);
        customerService.removeCustomer(customer);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/customers/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable("customerId") Long customerId, @RequestBody CustomerDto customerDto){
        Customer updatedCustomer = customerService.updateCustomer(customerId, customerDto);
        return ResponseEntity.ok(customerMapper.mapTo(updatedCustomer));
    }

}
