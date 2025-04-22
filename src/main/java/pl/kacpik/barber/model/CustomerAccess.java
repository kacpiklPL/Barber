package pl.kacpik.barber.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "customer_access")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomerAccess {

    @Id
    @Column(unique = true)
    @Builder.Default
    private String token = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private LocalDate lastLogin;

}
