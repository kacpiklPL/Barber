package pl.kacpik.barber.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.kacpik.barber.model.ReservationCompanyService;

import java.util.List;

@Repository
public interface ReservationCompanyServiceRepository extends CrudRepository<ReservationCompanyService, Long> {

    List<ReservationCompanyService> findAllByReservationId(long reservationId);

}
