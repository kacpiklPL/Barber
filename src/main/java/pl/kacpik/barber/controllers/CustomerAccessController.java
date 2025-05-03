package pl.kacpik.barber.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.CustomerAccess;
import pl.kacpik.barber.services.CustomerAccessService;
import pl.kacpik.barber.services.CustomerService;
import pl.kacpik.barber.utils.QRCodeGenerator;


@RestController
public class CustomerAccessController {

    private final CustomerAccessService customerAccessService;

    private final CustomerService customerService;

    private QRCodeGenerator qrCodeGenerator;

    @Autowired
    public CustomerAccessController(CustomerAccessService customerAccessService, CustomerService customerService, QRCodeGenerator qrCodeGenerator) {
        this.customerAccessService = customerAccessService;
        this.customerService = customerService;
        this.qrCodeGenerator = qrCodeGenerator;
    }

    @GetMapping(path = "/customers/{customerId}/qrcode")
    public ResponseEntity<byte[]> getCustomerAccess(@PathVariable Long customerId){
        Customer customer = customerService.getCustomerById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
        CustomerAccess customerAccess = customerAccessService.getOrCreateCustomerAccess(customer);

        try{
            byte[] data = qrCodeGenerator.createQRCode("https://tapujemy.pl/qrcode/" + customerAccess.getToken());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(data);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code", e);
        }
    }
}
