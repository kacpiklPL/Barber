package pl.kacpik.barber.services;

import pl.kacpik.barber.model.User;
import pl.kacpik.barber.model.dto.LoginRequest;
import pl.kacpik.barber.model.dto.LoginResponse;

import java.util.Optional;

public interface AuthenticationService {

    Optional<User> getUserByPhoneNumber(String phoneNumber);

    Optional<User> getUserByEmail(String email);

    User register(User user);

    LoginResponse authentication(LoginRequest loginRequest);

}
