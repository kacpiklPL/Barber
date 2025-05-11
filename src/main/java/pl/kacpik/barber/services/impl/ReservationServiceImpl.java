package pl.kacpik.barber.services.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kacpik.barber.exceptions.ReservationConflictException;
import pl.kacpik.barber.model.CompanyService;
import pl.kacpik.barber.model.Reservation;
import pl.kacpik.barber.model.ReservationCompanyService;
import pl.kacpik.barber.model.dto.ReservationCompanyServiceDto;
import pl.kacpik.barber.model.dto.ReservationServicesUpdateDto;
import pl.kacpik.barber.repositories.CompanyServiceRepository;
import pl.kacpik.barber.repositories.ReservationRepository;
import pl.kacpik.barber.services.CompanyServiceService;
import pl.kacpik.barber.services.ReservationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    private final CompanyServiceService companyServiceService;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, CompanyServiceService companyServiceService) {
        this.reservationRepository = reservationRepository;
        this.companyServiceService = companyServiceService;
    }

    @Override
    public Reservation addReservation(Reservation reservation) {
        if(existsOverlappingReservation(reservation.getEmployee().getId(), reservation.getEndTime(), reservation.getStartTime())){
            throw new ReservationConflictException("Employee already has a reservation in this time slot.");
        }
        for (ReservationCompanyService reservationCompanyService : reservation.getReservationCompanyServices()) {
            companyServiceService.addCompanyService(reservationCompanyService.getCompanyService());
        }
        return reservationRepository.save(reservation);
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    @Override
    public void deleteReservation(long reservationId) {
        Reservation reservation = getReservationOrThrow(reservationId);
        deleteReservation(reservation);
    }

    @Override
    public Optional<Reservation> getReservationById(long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @Override
    public void updateClientAttended(long reservationId, boolean value) {
        Reservation reservation = getReservationOrThrow(reservationId);
        reservation.setClientAttended(value);
    }

    @Override
    public void updateTime(long reservationId, LocalDateTime startTime, LocalDateTime endTime) {
        Reservation reservation = getReservationOrThrow(reservationId);

        if(existsOverlappingReservationWithoutReservationId(reservation.getEmployee().getId(), reservation.getId(), endTime, startTime)){
            throw new ReservationConflictException("Employee already has a reservation in this time slot.");
        }

        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
    }

    @Override
    public void updateServices(long reservationId, ReservationServicesUpdateDto servicesUpdateDto) {
        Reservation reservation = getReservationOrThrow(reservationId);
        clearExistingServices(reservation);
        reservation.setReservationCompanyServices(buildNewServices(servicesUpdateDto, reservation));
    }

    @Override
    public Reservation getReservationOrThrow(long reservationId){
        return getReservationById(reservationId).orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));
    }

    private boolean existsOverlappingReservation(long employeeId, LocalDateTime endTime, LocalDateTime startTime){
        return reservationRepository.existsByEmployeeIdAndStartTimeLessThanAndEndTimeGreaterThan(employeeId, endTime, startTime);
    }

    private boolean existsOverlappingReservationWithoutReservationId(long employeeId, long reservationId, LocalDateTime endTime, LocalDateTime startTime){
        return reservationRepository.existsByEmployeeIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(employeeId, reservationId, endTime, startTime);
    }

    private void clearExistingServices(Reservation reservation){
        reservation.getReservationCompanyServices().clear();
    }

    private Set<ReservationCompanyService> buildNewServices(ReservationServicesUpdateDto servicesUpdateDto, Reservation reservation){
        return servicesUpdateDto.getServices().stream()
                .map((serviceDto) -> {
                    CompanyService companyService = companyServiceService.getCompanyServiceById(serviceDto.getCompanyServiceId())
                            .orElseThrow(() -> new ReservationConflictException("CompanyService not found with id: " + serviceDto.getCompanyServiceId()));
                    return ReservationCompanyService.builder()
                            .companyService(companyService)
                            .price(serviceDto.getPrice())
                            .reservation(reservation)
                            .build();
                }).collect(Collectors.toSet());
    }
}
