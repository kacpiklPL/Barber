package pl.kacpik.barber.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kacpik.barber.exceptions.DuplicateEmailException;
import pl.kacpik.barber.exceptions.DuplicatePhoneNumberException;
import pl.kacpik.barber.model.User;
import pl.kacpik.barber.model.dto.LoginRequest;
import pl.kacpik.barber.model.dto.LoginResponse;
import pl.kacpik.barber.repositories.AuthenticationRepository;
import pl.kacpik.barber.services.AuthenticationService;
import pl.kacpik.barber.services.JwtService;

import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationRepository authenticationRepository;

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationRepository authenticationRepository, AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.authenticationRepository = authenticationRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        return authenticationRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return authenticationRepository.findByEmail(email);
    }

    @Override
    public User register(User user) {
        if(existsByPhoneNumber(user.getPhoneNumber())){
            throw new DuplicatePhoneNumberException("Phone number already in use: " + user.getPhoneNumber());
        }
        if(existsByEmail(user.getEmail())){
            throw new DuplicateEmailException("Email already in use: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return authenticationRepository.save(user);
    }

    @Override
    public LoginResponse authentication(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return new LoginResponse(token);
    }

    private boolean existsByPhoneNumber(String phoneNumber){
        return getUserByPhoneNumber(phoneNumber).isPresent();
    }

    private boolean existsByEmail(String email){
        return getUserByEmail(email).isPresent();
    }


}
