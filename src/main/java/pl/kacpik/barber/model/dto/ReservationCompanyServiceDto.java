package pl.kacpik.barber.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationCompanyServiceDto {

    private long companyServiceId;

    private BigDecimal price;

}
