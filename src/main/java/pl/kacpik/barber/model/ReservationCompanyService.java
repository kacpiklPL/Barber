package pl.kacpik.barber.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "reservation_company_service")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ReservationCompanyService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "company_service_id")
    private CompanyService companyService;

    private BigDecimal price;

}
