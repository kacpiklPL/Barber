package pl.kacpik.barber.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.kacpik.barber.model.Reservation;
import pl.kacpik.barber.model.dto.ReservationDto;

@Component
public class ReservationMapperImpl implements Mapper<Reservation, ReservationDto> {

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public ReservationDto mapTo(Reservation reservation) {
        return modelMapper.map(reservation, ReservationDto.class);
    }

    @Override
    public Reservation mapFrom(ReservationDto reservationDto) {
        return modelMapper.map(reservationDto, Reservation.class);
    }
}
