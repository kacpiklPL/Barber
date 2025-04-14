package pl.kacpik.barber.services;

import pl.kacpik.barber.model.Reservation;

public interface ReservationService {

    Reservation addReservation(Reservation reservation);

    void removeReservation(Reservation reservation);

}
