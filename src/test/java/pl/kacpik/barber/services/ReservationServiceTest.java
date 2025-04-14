package pl.kacpik.barber.services;

import jakarta.transaction.Transactional;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kacpik.barber.model.*;
import pl.kacpik.barber.repositories.CompanyServiceRepository;
import pl.kacpik.barber.repositories.ReservationCompanyServiceRepository;
import pl.kacpik.barber.repositories.ReservationRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
    public void shouldRemoveReservationFromDatabase(){
        Customer customer = createCustomer();
        Employee employee = createEmployee();
        Reservation reservation = createReservationWithService(customer, employee);
        Reservation savedReservation = reservationService.addReservation(reservation);

        reservationService.removeReservation(savedReservation);

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
}
