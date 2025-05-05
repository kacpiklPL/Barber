package pl.kacpik.barber.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kacpik.barber.mappers.UserMapperImpl;
import pl.kacpik.barber.model.User;
import pl.kacpik.barber.model.dto.LoginRequest;
import pl.kacpik.barber.model.dto.LoginResponse;
import pl.kacpik.barber.model.dto.RegisterRequest;
import pl.kacpik.barber.model.dto.UserDto;
import pl.kacpik.barber.services.AuthenticationService;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final UserMapperImpl userMapper;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, UserMapperImpl userMapper) {
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest registerRequest){
        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .lastName(registerRequest.getLastName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .role("CUSTOMER")
                .password(registerRequest.getPassword()).build();
        User savedUser = authenticationService.register(user);
        return new ResponseEntity<>(userMapper.mapTo(savedUser), HttpStatus.CREATED);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse = authenticationService.authentication(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

}
