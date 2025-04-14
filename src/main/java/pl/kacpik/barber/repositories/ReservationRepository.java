package pl.kacpik.barber.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.kacpik.barber.model.Reservation;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {

}
