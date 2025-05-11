package pl.kacpik.barber.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kacpik.barber.mappers.ReservationMapperImpl;
import pl.kacpik.barber.model.Reservation;
import pl.kacpik.barber.model.dto.ReservationAttendedUpdateDto;
import pl.kacpik.barber.model.dto.ReservationDto;
import pl.kacpik.barber.model.dto.ReservationServicesUpdateDto;
import pl.kacpik.barber.model.dto.ReservationTimeUpdateDto;
import pl.kacpik.barber.services.ReservationService;

@RestController
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationMapperImpl reservationMapper;

    @PostMapping(path = "/reservations")
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationDto reservationDto){
        Reservation reservation = reservationMapper.mapFrom(reservationDto);
        Reservation savedReservation = reservationService.addReservation(reservation);
        return new ResponseEntity<>(reservationMapper.mapTo(savedReservation), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/reservations/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long reservationId){
        reservationService.deleteReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/reservations/{reservationId}/attended")
    public ResponseEntity<Void> updateReservationAttended(@PathVariable Long reservationId, @RequestBody ReservationAttendedUpdateDto attendedUpdateDto){
        reservationService.updateClientAttended(reservationId, attendedUpdateDto.isClientAttended());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/reservations/{reservationId}/time")
    public ResponseEntity<Void> updateReservationTime(@PathVariable Long reservationId, @RequestBody ReservationTimeUpdateDto timeUpdateDto){
        reservationService.updateTime(reservationId, timeUpdateDto.getStartTime(), timeUpdateDto.getEndTime());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/reservations/{reservationId}/services")
    public ResponseEntity<Void> updateReservationServices(@PathVariable Long reservationId, @RequestBody ReservationServicesUpdateDto servicesUpdateDto){
        reservationService.updateServices(reservationId, servicesUpdateDto);
        return ResponseEntity.noContent().build();
    }
}
