package pl.kacpik.barber.controlers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kacpik.barber.mappers.UserMapperImpl;
import pl.kacpik.barber.model.User;
import pl.kacpik.barber.model.dto.RegisterRequest;
import pl.kacpik.barber.model.dto.UserDto;
import pl.kacpik.barber.services.AuthenticationService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Autowired
    private UserMapperImpl userMapper;

    private User createUser(String phoneNumber, String email){
        return User.builder()
                .id(1L)
                .name("Kacper")
                .lastName("Nowak")
                .phoneNumber(phoneNumber)
                .password("abc123")
                .role("CUSTOMER")
                .email(email)
                .build();
    }

    private RegisterRequest createRegisterRequest(String phoneNumber, String email){
        return RegisterRequest.builder()
                .name("Kacper")
                .lastName("Nowak")
                .phoneNumber(phoneNumber)
                .email(email)
                .build();
    }

    private ResultActions postRegisterUser(RegisterRequest registerRequest) throws Exception {
        String content = objectMapper.writeValueAsString(registerRequest);
        return mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );
    }

    @Test
    public void shouldRegisterNewUserAndReturnSavedUser() throws Exception {
        RegisterRequest registerRequest = createRegisterRequest("000000000", "test@gmail.com");
        User user = createUser(registerRequest.getPhoneNumber(), registerRequest.getEmail());

        when(authenticationService.register(any())).thenReturn(user);

        MvcResult result = postRegisterUser(registerRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        UserDto resultUserDto = objectMapper.readValue(json, UserDto.class);
        assertThat(resultUserDto, notNullValue());
        assertThat(resultUserDto.getId(), is(user.getId()));
        assertThat(resultUserDto.getName(), is(registerRequest.getName()));
        assertThat(resultUserDto.getLastName(), is(registerRequest.getLastName()));
        assertThat(resultUserDto.getPhoneNumber(), is(registerRequest.getPhoneNumber()));
        assertThat(resultUserDto.getEmail(), is(registerRequest.getEmail()));
    }

}
