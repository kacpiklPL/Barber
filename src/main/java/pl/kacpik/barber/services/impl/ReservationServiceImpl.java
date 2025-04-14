package pl.kacpik.barber.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kacpik.barber.model.Reservation;
import pl.kacpik.barber.model.ReservationCompanyService;
import pl.kacpik.barber.repositories.CompanyServiceRepository;
import pl.kacpik.barber.repositories.ReservationRepository;
import pl.kacpik.barber.services.ReservationService;

import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CompanyServiceRepository companyServiceRepository;

    @Override
    public Reservation addReservation(Reservation reservation) {
        for (ReservationCompanyService reservationCompanyService : reservation.getReservationCompanyServices()) {
            companyServiceRepository.save(reservationCompanyService.getCompanyService());
        }
        return reservationRepository.save(reservation);
    }

    @Override
    public void removeReservation(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    @Override
    public Optional<Reservation> getReservationById(long reservationId) {
        return reservationRepository.findById(reservationId);
    }
}
