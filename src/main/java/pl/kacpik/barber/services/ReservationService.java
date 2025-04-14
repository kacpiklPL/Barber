package pl.kacpik.barber.services;

import pl.kacpik.barber.model.Reservation;

import java.util.Optional;

public interface ReservationService {

    Reservation addReservation(Reservation reservation);

    void removeReservation(Reservation reservation);

    Optional<Reservation> getReservationById(long reservationId);

}
