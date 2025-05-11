package pl.kacpik.barber.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kacpik.barber.exceptions.ReservationConflictException;
import pl.kacpik.barber.model.*;
import pl.kacpik.barber.model.dto.ReservationCompanyServiceDto;
import pl.kacpik.barber.model.dto.ReservationServicesUpdateDto;
import pl.kacpik.barber.repositories.CompanyServiceRepository;
import pl.kacpik.barber.repositories.ReservationCompanyServiceRepository;
import pl.kacpik.barber.repositories.ReservationRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ReservationServiceTest {

    @Autowired
    private ReservationCompanyServiceRepository reservationCompanyServiceRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CompanyServiceRepository companyServiceRepository;

    private Customer createCustomer(){
        Customer customer = Customer.builder()
                .name("Kacper")
                .lastName("Nowak")
                .phoneNumber("000000000")
                .build();
        return customerService.addCustomer(customer);
    }

    private Employee createEmployee(){
        Employee employee = Employee.builder()
                .pesel("00000000000")
                .birthday(LocalDate.of(2000, 1, 27))
                .name("Kacper")
                .lastName("Xyz")
                .build();
        return employeeService.addEmployee(employee);
    }

    private ReservationCompanyService createService(Reservation reservation, Employee employee){
        CompanyService companyService = CompanyService.builder()
                .employee(employee)
                .duration(Duration.ofMinutes(10).toMillis())
                .name("Strzyżenie brody")
                .price(new BigDecimal(40))
                .build();

        return ReservationCompanyService.builder()
                .price(companyService.getPrice())
                .companyService(companyService)
                .reservation(reservation).build();
    }

    private Reservation createReservationWithService(Customer customer, Employee employee) {
        Reservation reservation = Reservation.builder()
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(1))
                .customer(customer)
                .employee(employee)
                .build();

        ReservationCompanyService service = createService(reservation, employee);
        Set<ReservationCompanyService> services = new HashSet<>();
        services.add(service);
        reservation.setReservationCompanyServices(services);

        return reservation;
    }

    @Transactional
    @Test
    public void shouldAddCompleteReservationToDatabase(){
        Customer customer = createCustomer();
        Employee employee = createEmployee();

        Reservation reservation = createReservationWithService(customer, employee);

        Reservation savedReservation = reservationService.addReservation(reservation);

        Iterable<Reservation> result = reservationRepository.findAll();
        AssertionsForInterfaceTypes.assertThat(result)
                .hasSize(1)
                .containsExactly(savedReservation);
        Set<ReservationCompanyService> services = savedReservation.getReservationCompanyServices();
        assertThat(services, hasSize(1));
        ReservationCompanyService actual = services.iterator().next();
        assertThat(actual, is(reservation.getReservationCompanyServices().iterator().next()));
    }

    @Transactional
    @Test
    public void shouldDeleteReservationFromDatabase(){
        Customer customer = createCustomer();
        Employee employee = createEmployee();
        Reservation reservation = createReservationWithService(customer, employee);
        Reservation savedReservation = reservationService.addReservation(reservation);

        reservationService.deleteReservation(savedReservation);

        Optional<Reservation> resultReservation = reservationRepository.findById(savedReservation.getId());
        assertThat(resultReservation.isEmpty(), is(true));

        Iterable<ReservationCompanyService> result = reservationCompanyServiceRepository.findAll();
        AssertionsForInterfaceTypes.assertThat(result)
                .hasSize(0);

        List<ReservationCompanyService> resultServices = reservationCompanyServiceRepository.findAllByReservationId(savedReservation.getId());
        assertThat(resultServices, hasSize(0));

        assertThat(customerService.getCustomerById(customer.getId()).isPresent(), is(true));
        assertThat(employeeService.getEmployeeById(employee.getId()).isPresent(), is(true));
    }

    @Transactional
    @Test
    public void shouldNotRemoveCustomerWhenReservationIsDeleted(){
        Customer customer = createCustomer();
        Employee employee = createEmployee();
        Reservation reservation = createReservationWithService(customer, employee);
        reservationService.addReservation(reservation);

        Optional<Customer> resultCustomer = customerService.getCustomerById(customer.getId());
        assertThat(resultCustomer.isPresent(), is(true));
        assertThat(resultCustomer.get(), equalTo(customer));
    }

    @Transactional
    @Test
    public void shouldNotRemoveEmployeeWhenReservationIsDeleted(){
        Customer customer = createCustomer();
        Employee employee = createEmployee();
        Reservation reservation = createReservationWithService(customer, employee);
        reservationService.addReservation(reservation);

        Optional<Employee> resultEmployee = employeeService.getEmployeeById(employee.getId());
        assertThat(resultEmployee.isPresent(), is(true));
        assertThat(resultEmployee.get(), equalTo(employee));
    }

    @Transactional
    @Test
    public void shouldFindReservationById(){
        Customer customer = createCustomer();
        Employee employee = createEmployee();
        Reservation reservation = createReservationWithService(customer, employee);
        Reservation savedReservation = reservationService.addReservation(reservation);

        Optional<Reservation> resultReservation = reservationService.getReservationById(savedReservation.getId());
        assertThat(resultReservation.isPresent(), is(true));
        assertThat(resultReservation.get(), equalTo(reservation));
    }

    private Reservation createReservation(Employee employee, Customer customer, LocalDateTime startTime, LocalDateTime endTime){
        return Reservation.builder()
                .startTime(startTime)
                .endTime(endTime)
                .employee(employee)
                .customer(customer)
                .reservationCompanyServices(new HashSet<>())
                .clientAttended(false)
                .build();
    }

    @Transactional
    @Test
    public void shouldCreateNonOverlappingReservationsSuccessful(){
        Employee employee = createEmployee();
        Customer customer = createCustomer();
        Reservation reservation_1 = createReservation(
                employee,
                customer,
                LocalDateTime.of(2025, 1, 27, 15, 0, 0),
                LocalDateTime.of(2025, 1, 27, 15, 10, 0)
        );
        Reservation reservation_2 = createReservation(
                employee,
                customer,
                LocalDateTime.of(2025, 1, 27, 15, 10, 1),
                LocalDateTime.of(2025, 1, 27, 15, 20, 0)
        );

        reservationService.addReservation(reservation_1);
        reservationService.addReservation(reservation_2);
    }

    @Transactional
    @Test
    public void shouldThrowExceptionWhenCreateOverlappingReservations(){
        Employee employee = createEmployee();
        Customer customer = createCustomer();
        Reservation reservation_1 = createReservation(
                employee,
                customer,
                LocalDateTime.of(2025, 1, 27, 15, 0, 0),
                LocalDateTime.of(2025, 1, 27, 15, 10, 0)
        );
        Reservation reservation_2 = createReservation(
                employee,
                customer,
                LocalDateTime.of(2025, 1, 27, 15, 5, 0),
                LocalDateTime.of(2025, 1, 27, 15, 20, 0)
        );

        reservationService.addReservation(reservation_1);
        assertThrows(ReservationConflictException.class, () -> reservationService.addReservation(reservation_2));
    }


    @Test
    @Transactional
    public void shouldUpdateTimeReservationSuccessful(){
        LocalDateTime updateStartTime = LocalDateTime.of(2025, 1, 27, 19, 0, 0);
        LocalDateTime updateEndTime = LocalDateTime.of(2025, 1, 27, 19, 30, 0);
        Employee employee = createEmployee();
        Customer customer = createCustomer();
        Reservation reservation_1 = createReservation(
                employee,
                customer,
                LocalDateTime.of(2025, 1, 27, 15, 0, 0),
                LocalDateTime.of(2025, 1, 27, 15, 10, 0)
        );
        Reservation savedReservation = reservationService.addReservation(reservation_1);

        reservationService.updateTime(savedReservation.getId(), updateStartTime, updateEndTime);

        Optional<Reservation> optionalReservation = reservationService.getReservationById(savedReservation.getId());
        assertThat(optionalReservation.isPresent(), is(true));
        Reservation resultReservation = optionalReservation.get();
        assertThat(resultReservation.getStartTime(), equalTo(updateStartTime));
        assertThat(resultReservation.getEndTime(), equalTo(updateEndTime));
    }

    @Test
    public void shouldThrowWhenUpdatingTimeNonExistingReservation(){
        LocalDateTime updateStartTime = LocalDateTime.of(2025, 1, 27, 19, 0, 0);
        LocalDateTime updateEndTime = LocalDateTime.of(2025, 1, 27, 19, 30, 0);

        assertThrows(EntityNotFoundException.class, () -> reservationService.updateTime(100, updateStartTime, updateEndTime));
    }

    @Transactional
    @Test
    public void shouldThrowWhenUpdatingReservationTimeAndTimeSlotIsOverlapping(){
        Employee employee = createEmployee();
        Customer customer = createCustomer();
        Reservation reservation_1 = createReservation(
                employee,
                customer,
                LocalDateTime.of(2025, 1, 27, 15, 0, 0),
                LocalDateTime.of(2025, 1, 27, 15, 10, 0)
        );
        Reservation reservation_2 = createReservation(
                employee,
                customer,
                LocalDateTime.of(2025, 1, 27, 15, 15, 0),
                LocalDateTime.of(2025, 1, 27, 15, 20, 0)
        );
        Reservation savedReservation_1 = reservationService.addReservation(reservation_1);
        Reservation savedReservation_2 = reservationService.addReservation(reservation_2);

        reservationService.updateTime(
                savedReservation_1.getId(),
                LocalDateTime.of(2025, 1, 27, 15, 0, 0),
                LocalDateTime.of(2025, 1, 27, 15, 5, 0)
        );

        assertThrows(ReservationConflictException.class, () -> {
            reservationService.updateTime(
                    savedReservation_1.getId(),
                    LocalDateTime.of(2025, 1, 27, 15, 0, 0),
                    LocalDateTime.of(2025, 1, 27, 15, 25, 0)
            );
        });

        assertThrows(ReservationConflictException.class, () -> {
            reservationService.updateTime(
                    savedReservation_1.getId(),
                    LocalDateTime.of(2025, 1, 27, 15, 15, 0),
                    LocalDateTime.of(2025, 1, 27, 15, 30, 0)
            );
        });

    }

    private Set<ReservationCompanyService> buildServices(Reservation reservation, Employee employee){
        CompanyService companyService = createCompanyService("Strzyżenie męskie", employee);

        ReservationCompanyService reservationCompanyService = ReservationCompanyService.builder()
                .reservation(reservation)
                .companyService(companyService)
                .price(companyService.getPrice())
                .build();

        return new HashSet<>(List.of(reservationCompanyService));
    }

    private CompanyService createCompanyService(String name, Employee employee){
        return CompanyService.builder()
                .employee(employee)
                .price(BigDecimal.valueOf(2.13))
                .name(name)
                .duration(Duration.ofMinutes(20).toMillis())
                .build();
    }

    @Test
    @Transactional
    public void shouldUpdateServicesReservationSuccessful(){
        Employee employee = createEmployee();
        Customer customer = createCustomer();
        Reservation reservation_1 = createReservation(
                employee,
                customer,
                LocalDateTime.of(2025, 1, 27, 15, 0, 0),
                LocalDateTime.of(2025, 1, 27, 15, 10, 0)
        );
        Reservation savedReservation = reservationService.addReservation(reservation_1);

        CompanyService companyService = createCompanyService("Strzyżenie brody", employee);
        CompanyService savedCompanyService = companyServiceRepository.save(companyService);
        ReservationCompanyServiceDto companyServiceDto = ReservationCompanyServiceDto.builder()
                .companyServiceId(savedCompanyService.getId())
                .price(savedCompanyService.getPrice())
                .build();

        ReservationServicesUpdateDto servicesUpdateDto = ReservationServicesUpdateDto.builder()
                .services(new HashSet<>(List.of(companyServiceDto)))
                .build();
        reservationService.updateServices(savedReservation.getId(), servicesUpdateDto);

        Set<ReservationCompanyService> services = savedReservation.getReservationCompanyServices();
        assertThat(services, hasSize(1));
        assertThat(services.iterator().next().getCompanyService(), equalTo(companyService));
    }

    @Test
    public void shouldThrowWhenUpdatingServicesNonExistingReservation(){
        ReservationServicesUpdateDto servicesUpdateDto = ReservationServicesUpdateDto.builder()
                .services(new HashSet<>())
                .build();

        assertThrows(EntityNotFoundException.class, () -> {
           reservationService.updateServices(1L, servicesUpdateDto);
        });
    }

    @Test
    @Transactional
    public void shouldRemoveOldCompanyServiceFromReservation(){
        Employee employee = createEmployee();
        Customer customer = createCustomer();
        Reservation reservation_1 = createReservation(
                employee,
                customer,
                LocalDateTime.of(2025, 1, 27, 15, 0, 0),
                LocalDateTime.of(2025, 1, 27, 15, 10, 0)
        );
        reservation_1.setReservationCompanyServices(buildServices(reservation_1, employee));
        Reservation savedReservation = reservationService.addReservation(reservation_1);

        ReservationServicesUpdateDto servicesUpdateDto = ReservationServicesUpdateDto.builder()
                .services(new HashSet<>())
                .build();
        reservationService.updateServices(savedReservation.getId(), servicesUpdateDto);

        assertThat(savedReservation.getReservationCompanyServices(), empty());
    }

    private ReservationCompanyServiceDto buildReservationCompanyServiceDto(String serviceName, Employee employee){
        CompanyService companyService = createCompanyService(serviceName, employee);
        CompanyService savedCompanyService = companyServiceRepository.save(companyService);
        return ReservationCompanyServiceDto.builder()
                .companyServiceId(savedCompanyService.getId())
                .price(BigDecimal.valueOf(1))
                .build();
    }

    private ReservationCompanyServiceDto buildNonExistingCompanyService(){
        return ReservationCompanyServiceDto.builder()
                .companyServiceId(100)
                .price(BigDecimal.valueOf(1))
                .build();
    }

    @Test
    @Transactional
    public void shouldThrowWhenUpdatingNonExistingCompanyService(){
        Employee employee = createEmployee();
        Customer customer = createCustomer();
        Reservation reservation_1 = createReservation(employee, customer, LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));
        Reservation savedReservation = reservationService.addReservation(reservation_1);

        ReservationCompanyServiceDto companyServiceDto_1 = buildReservationCompanyServiceDto("Strzyżenie włosów", employee);
        ReservationCompanyServiceDto companyServiceDto_2 = buildNonExistingCompanyService();

        ReservationServicesUpdateDto servicesUpdateDto = ReservationServicesUpdateDto.builder()
                .services(new HashSet<>(List.of(companyServiceDto_1, companyServiceDto_2)))
                .build();

        Set<ReservationCompanyService> services = savedReservation.getReservationCompanyServices();
        assertThrows(ReservationConflictException.class, () -> reservationService.updateServices(savedReservation.getId(), servicesUpdateDto));
        assertThat(services, hasSize(0));
    }

}
