package pl.kacpik.barber.controlers;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.CustomerAccess;
import pl.kacpik.barber.services.CustomerAccessService;
import pl.kacpik.barber.services.CustomerService;
import pl.kacpik.barber.utils.QRCodeGenerator;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerAccessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private CustomerAccessService customerAccessService;

    @MockitoBean
    private QRCodeGenerator qrCodeGenerator;

    private Customer createCustomer(long customerId){
        return Customer.builder()
                .id(customerId)
                .name("Kacper")
                .lastName("Nowak")
                .phoneNumber("000000000")
                .build();
    }

    private CustomerAccess createCustomerAccess(Customer customer){
        return CustomerAccess.builder()
                .token("ABCDEFG")
                .customer(customer)
                .build();

    }

    @Test
    public void shouldReturnQRCodeIfCustomerExists() throws Exception {
        long customerId = 1L;
        Customer customer = createCustomer(customerId);
        CustomerAccess customerAccess = createCustomerAccess(customer);
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customer));
        when(customerAccessService.getOrCreateCustomerAccess(customer)).thenReturn(customerAccess);

        mockMvc.perform(get("/customers/{customerId}/qrcode", customerId)
                .with(csrf())
                .contentType(MediaType.IMAGE_PNG))
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
        long customerId = 1L;
        Customer customer = createCustomer(customerId);
        CustomerAccess customerAccess = createCustomerAccess(customer);
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.empty());
        when(customerAccessService.getOrCreateCustomerAccess(customer)).thenReturn(customerAccess);

        mockMvc.perform(get("/customers/{customerId}/qrcode", customerId)
                        .with(csrf())
                        .contentType(MediaType.IMAGE_PNG))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnInternalErrorWhenQRCodeThrowException() throws Exception {
        long customerId = 1L;
        Customer customer = createCustomer(customerId);
        CustomerAccess customerAccess = createCustomerAccess(customer);
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customer));
        when(customerAccessService.getOrCreateCustomerAccess(customer)).thenReturn(customerAccess);

        when(qrCodeGenerator.createQRCode(any())).thenThrow(RuntimeException.class);

        mockMvc.perform(get("/customers/{customerId}/qrcode", customerId)
                        .with(csrf())
                        .contentType(MediaType.IMAGE_PNG))
                .andExpect(status().isInternalServerError());
    }
}