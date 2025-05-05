package pl.kacpik.barber.services;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kacpik.barber.exceptions.DuplicateEmailException;
import pl.kacpik.barber.exceptions.DuplicatePhoneNumberException;
import pl.kacpik.barber.model.User;
import pl.kacpik.barber.repositories.AuthenticationRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class AuthenticationServiceTest {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private User createUser(String phoneNumber, String email){
        return User.builder()
                .name("Kacper")
                .lastName("Nowak")
                .phoneNumber(phoneNumber)
                .role("CUSTOMER")
                .password("abc123")
                .email(email)
                .build();
    }

    @BeforeEach
    public void setUp() {
        authenticationRepository.deleteAll();
    }

    @Test
    public void shouldRegisterNewUser(){
        User user = createUser("000000000", "test@gmail.com");

        User savedUser = authenticationService.register(user);

        assertThat(savedUser, notNullValue());
        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getId(), greaterThan(0L));

        Iterable<User> result = authenticationRepository.findAll();
        AssertionsForInterfaceTypes.assertThat(result)
                .hasSize(1).
                containsExactly(savedUser);
    }

    @Test
    public void shouldThrowExceptionWhenSavingUserWithDuplicatePhoneNumber(){
        User user_1 = createUser("000000000", "test@gmail.com");
        User user_2 = createUser("000000000", "test2@gmail.com");
        authenticationService.register(user_1);

        assertThrows(DuplicatePhoneNumberException.class, () -> authenticationService.register(user_2));
    }

    @Test
    public void shouldThrowExceptionWhenSavingUserWithDuplicateEmail(){
        User user_1 = createUser("000000000", "test@gmail.com");
        User user_2 = createUser("000000001", "test@gmail.com");
        authenticationService.register(user_1);

        assertThrows(DuplicateEmailException.class, () -> authenticationService.register(user_2));
    }
}
