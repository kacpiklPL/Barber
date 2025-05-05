package pl.kacpik.barber.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.kacpik.barber.model.User;
import pl.kacpik.barber.model.dto.UserDto;

@Component
public class UserMapperImpl implements Mapper<User, UserDto> {

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public UserDto mapTo(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User mapFrom(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

}
