package pl.kacpik.barber.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Employee employee;

    private Customer customer;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean clientAttended;

}
