package pl.kacpik.barber.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.kacpik.barber.model.Reservation;

import java.time.LocalDateTime;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    boolean existsByEmployeeIdAndStartTimeLessThanAndEndTimeGreaterThan(long employeeId, LocalDateTime end, LocalDateTime start);

    boolean existsByEmployeeIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(long employeeId, long reservationId, LocalDateTime end, LocalDateTime start);

}
