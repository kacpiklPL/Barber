package pl.kacpik.barber.services;

import pl.kacpik.barber.model.Reservation;
import pl.kacpik.barber.model.dto.ReservationServicesUpdateDto;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ReservationService {

    Reservation addReservation(Reservation reservation);

    void deleteReservation(Reservation reservation);

    void deleteReservation(long reservationId);

    Optional<Reservation> getReservationById(long reservationId);

    Reservation getReservationOrThrow(long reservationId);

    void updateClientAttended(long reservationId, boolean value);

    void updateTime(long reservationId, LocalDateTime startTime, LocalDateTime endTime);

    void updateServices(long reservationId, ReservationServicesUpdateDto servicesUpdateDto);

}
