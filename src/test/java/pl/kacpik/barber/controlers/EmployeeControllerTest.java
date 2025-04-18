package pl.kacpik.barber.controlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kacpik.barber.mappers.EmployeeMapperImpl;
import pl.kacpik.barber.model.Employee;
import pl.kacpik.barber.model.dto.EmployeeDto;
import pl.kacpik.barber.services.EmployeeService;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeMapperImpl employeeMapper;

    private EmployeeDto createEmployeeDto(){
        return EmployeeDto.builder()
                .name("Kacper")
                .lastName("Nowak")
                .pesel("00000000000")
                .birthday(LocalDate.of(2000, 1, 27))
                .build();
    }

    @Test
    public void shouldCreateAndReturnEmployee() throws Exception{
        EmployeeDto employeeDto = createEmployeeDto();
        String content = objectMapper.writeValueAsString(employeeDto);

        MvcResult result = mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        EmployeeDto resultEmployeeDto = objectMapper.readValue(json, EmployeeDto.class);

        assertThat(resultEmployeeDto, notNullValue());
        assertThat(resultEmployeeDto.getId(), notNullValue());
        assertThat(resultEmployeeDto.getId(), greaterThan(0L));
        assertThat(resultEmployeeDto.getName(), equalTo(employeeDto.getName()));
        assertThat(resultEmployeeDto.getLastName(), equalTo(employeeDto.getLastName()));
        assertThat(resultEmployeeDto.getPesel(), equalTo(employeeDto.getPesel()));
        assertThat(resultEmployeeDto.getBirthday(), equalTo(employeeDto.getBirthday()));
    }

    @Test
    public void shouldReturnEmployeeWhenExists() throws Exception{
        Employee employee = Employee.builder()
                .name("name")
                .lastName("lastname")
                .pesel("123123123")
                .birthday(LocalDate.of(2000, 1, 27))
                .build();
        Employee savedEmployee = employeeService.addEmployee(employee);

        MvcResult result = mockMvc.perform(get("/employees/{employeeId}", savedEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        EmployeeDto resultEmployeeDto = objectMapper.readValue(json, EmployeeDto.class);
        Employee resultEmployee = employeeMapper.mapFrom(resultEmployeeDto);

        assertThat(resultEmployeeDto, notNullValue());
        assertThat(resultEmployee, equalTo(savedEmployee));
    }

    @Test
    public void shouldReturnNotFoundWhenEmployeeNotExists() throws Exception {
        mockMvc.perform(get("/employees/{employeeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound());
    }

}
