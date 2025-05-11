package pl.kacpik.barber.controlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kacpik.barber.exceptions.ReservationConflictException;
import pl.kacpik.barber.mappers.ReservationMapperImpl;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.Employee;
import pl.kacpik.barber.model.Reservation;
import pl.kacpik.barber.model.dto.ReservationAttendedUpdateDto;
import pl.kacpik.barber.model.dto.ReservationDto;
import pl.kacpik.barber.model.dto.ReservationTimeUpdateDto;
import pl.kacpik.barber.repositories.CustomerRepository;
import pl.kacpik.barber.repositories.EmployeeRepository;
import pl.kacpik.barber.services.ReservationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ReservationMapperImpl reservationMapper;

    @MockitoBean
    private ReservationService reservationService;

    @AfterEach
    public void setUp(){
        customerRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    private Customer createCustomer(){
        Customer customer = Customer.builder()
                .name("Kacper")
                .lastName("Nowak")
                .phoneNumber("000000000")
                .build();
        return customerRepository.save(customer);
    }

    private Employee createEmployee(){
        Employee employee = Employee.builder()
                .name("Kacper")
                .lastName("Nowak")
                .pesel("00000000000")
                .birthday(LocalDate.of(2000, 1, 27))
                .build();
        return employeeRepository.save(employee);
    }

    private Reservation createReservation(Customer customer, Employee employee){
        return Reservation.builder()
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(10))
                .employee(employee)
                .customer(customer)
                .reservationCompanyServices(new HashSet<>())
                .clientAttended(false)
                .build();
    }

    @Test
    public void shouldCreateReservationAndReturn() throws Exception{
        Customer customer = createCustomer();
        Employee employee = createEmployee();
        Reservation reservation = createReservation(customer, employee);
        ReservationDto reservationDto = reservationMapper.mapTo(reservation);
        reservation.setId(2L);

        when(reservationService.addReservation(any())).thenReturn(reservation);

        String content = objectMapper.writeValueAsString(reservationDto);
        MvcResult result = mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isCreated()).andReturn();

        String resultContent = result.getResponse().getContentAsString();
        ReservationDto resultReservationDto = objectMapper.readValue(resultContent, ReservationDto.class);
        assertThat(resultReservationDto, notNullValue());
        assertThat(resultReservationDto.getId(), is(2L));
    }

    @Test
    public void shouldReturnNotFoundWhenDeletingNonExistingReservation() throws Exception {
        doThrow(new EntityNotFoundException(""))
                .when(reservationService).deleteReservation(1L);

        mockMvc.perform(delete("/reservations/{reservationId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteReservationSuccessfulIfExists() throws Exception {
        Customer customer = createCustomer();
        Employee employee = createEmployee();
        Reservation reservation = createReservation(customer, employee);

        when(reservationService.getReservationById(1L)).thenReturn(Optional.of(reservation));

        mockMvc.perform(delete("/reservations/{reservationId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnConflictWhenCreateOverlappingReservation() throws Exception {
        Customer customer = createCustomer();
        Employee employee = createEmployee();
        Reservation reservation = createReservation(customer, employee);
        ReservationDto reservationDto = reservationMapper.mapTo(reservation);

        when(reservationService.addReservation(any())).thenThrow(new ReservationConflictException(""));

        String content = objectMapper.writeValueAsString(reservationDto);
        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isConflict());
    }

    @Test
    public void shouldUpdateReservationAttendedSuccessful() throws Exception {
        Customer customer = createCustomer();
        Employee employee = createEmployee();
        Reservation reservation = createReservation(customer, employee);
        ReservationAttendedUpdateDto attendedUpdateDto = new ReservationAttendedUpdateDto(true);

        when(reservationService.getReservationById(1L)).thenReturn(Optional.of(reservation));
        String content = objectMapper.writeValueAsString(attendedUpdateDto);

        mockMvc.perform(patch("/reservations/1/attended")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)

        ).andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundWhenUpdatingReservationAttended() throws Exception {
        ReservationAttendedUpdateDto attendedUpdateDto = new ReservationAttendedUpdateDto(true);

        doThrow(new EntityNotFoundException(""))
                .when(reservationService).updateClientAttended(1L, true);

        String content = objectMapper.writeValueAsString(attendedUpdateDto);
        mockMvc.perform(patch("/reservations/1/attended")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdateReservationTimeSuccessful() throws  Exception {
        ReservationTimeUpdateDto timeUpdateDto = ReservationTimeUpdateDto.builder()
                .startTime(LocalDateTime.of(2025, 1, 27, 10, 0))
                .endTime(LocalDateTime.of(2025, 1, 27, 10, 15))
                .build();

        doNothing().when(reservationService).updateTime(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));

        String content = objectMapper.writeValueAsString(timeUpdateDto);
        mockMvc.perform(patch("/reservations/1/time")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundWhenUpdatingNonExistingReservationTime() throws  Exception {
        ReservationTimeUpdateDto timeUpdateDto = ReservationTimeUpdateDto.builder()
                .startTime(LocalDateTime.of(2025, 1, 27, 10, 0))
                .endTime(LocalDateTime.of(2025, 1, 27, 10, 15))
                .build();

        doThrow(new EntityNotFoundException(""))
                .when(reservationService).updateTime(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));

        String content = objectMapper.writeValueAsString(timeUpdateDto);
        mockMvc.perform(patch("/reservations/1/time")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundWhenUpdatingReservationTimeAndTimeSlotIsOverlapping() throws  Exception {
        ReservationTimeUpdateDto timeUpdateDto = ReservationTimeUpdateDto.builder()
                .startTime(LocalDateTime.of(2025, 1, 27, 10, 0))
                .endTime(LocalDateTime.of(2025, 1, 27, 10, 15))
                .build();

        doThrow(new ReservationConflictException(""))
                .when(reservationService).updateTime(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));

        String content = objectMapper.writeValueAsString(timeUpdateDto);
        mockMvc.perform(patch("/reservations/1/time")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isConflict());
    }

}
