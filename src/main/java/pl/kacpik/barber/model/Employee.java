package pl.kacpik.barber.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "employees")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pesel;

    private String name;

    private String lastName;

    private LocalDate birthday;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompanyService> services;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reservation> reservations;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(getId(), employee.getId()) && Objects.equals(getPesel(), employee.getPesel()) && Objects.equals(getName(), employee.getName()) && Objects.equals(getLastName(), employee.getLastName()) && Objects.equals(getBirthday(), employee.getBirthday());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPesel(), getName(), getLastName(), getBirthday());
    }
}
