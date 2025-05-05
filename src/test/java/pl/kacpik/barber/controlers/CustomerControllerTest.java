package pl.kacpik.barber.controlers;

import com.google.gson.Gson;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kacpik.barber.mappers.CustomerMapperImpl;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.dto.CustomerDto;
import pl.kacpik.barber.services.CustomerService;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private CustomerMapperImpl customerMapper;

    @Test
    public void shouldCreateNewCustomer() throws Exception{
        String jsonValue = """
                {
                    "name": "Kacper",
                    "lastName": "Nowak",
                    "phoneNumber": "000000000"
                }
                """;

        mockMvc.perform(post("/api/employees/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonValue)
                )
                .andExpect(status().isCreated());
    }

    private Customer createCustomer(){
        return Customer.builder()
                .id(1L)
                .name("Kacper")
                .lastName("Nowak")
                .phoneNumber("000000000")
                .build();
    }

    private CustomerDto createCustomerDto(){
        return CustomerDto.builder()
                .id(1L)
                .name("Kacper")
                .lastName("Nowak")
                .phoneNumber("000000000").build();
    }

    @Test
    public void shouldReturnCustomerWhenExists() throws Exception {
        Customer customer = createCustomer();
        CustomerDto customerDto = createCustomerDto();

        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.mapTo(customer)).thenReturn(customerDto);

        MvcResult result = mockMvc.perform(get("/api/employees/customers/{customerId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        CustomerDto resultCustomerDto = new Gson().fromJson(json, CustomerDto.class);

        assertThat(resultCustomerDto, notNullValue());
        assertThat(resultCustomerDto.getId(), equalTo(1L));
        assertThat(resultCustomerDto.getName(), equalTo("Kacper"));
        assertThat(resultCustomerDto.getLastName(), equalTo("Nowak"));
        assertThat(resultCustomerDto.getPhoneNumber(), equalTo("000000000"));
    }

    @Test
    public void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
        long nonExistentCustomerId = 1L;
        when(customerService.getCustomerById(nonExistentCustomerId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/employees/customers/{customerId}", nonExistentCustomerId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldRemoveCustomerSuccessfulIfExists() throws Exception {
        Customer customer = createCustomer();
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(customer));

        mockMvc.perform(delete("/api/employees/customers/{customerId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundWhenDeletingNonExistingConsumer() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/employees/customers/{customerId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdateCustomerSuccessfulIfExists() throws Exception {
        Customer customer = createCustomer();
        CustomerDto customerDto = createCustomerDto();

        when(customerService.updateCustomer(1L, customerDto)).thenReturn(customer);
        when(customerMapper.mapTo(customer)).thenReturn(customerDto);

        mockMvc.perform(put("/api/employees/customers/{customerId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(customerDto))
                )
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotFoundWhenUpdatingNonExistingConsumer() throws Exception {
        CustomerDto customerDto = createCustomerDto();
        when(customerService.updateCustomer(1L, customerDto)).thenThrow(new EntityNotFoundException());

        mockMvc.perform(put("/api/employees/customers/{customerId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(customerDto))
                )
                .andExpect(status().isNotFound());
    }

}
