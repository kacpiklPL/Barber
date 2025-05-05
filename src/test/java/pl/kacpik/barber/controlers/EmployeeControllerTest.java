package pl.kacpik.barber.controlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
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

    private Employee createEmployee(){
        return Employee.builder()
                .name("Kacper")
                .lastName("Nowak")
                .pesel("00000000000")
                .birthday(LocalDate.of(2000, 1, 27))
                .build();
    }

    @Test
    public void shouldCreateAndReturnEmployee() throws Exception{
        EmployeeDto employeeDto = createEmployeeDto();
        Employee employee = employeeMapper.mapFrom(employeeDto);
        employee.setId(1L);
        String content = objectMapper.writeValueAsString(employeeDto);

        when(employeeService.addEmployee(ArgumentMatchers.any())).thenReturn(employee);

        MvcResult result = mockMvc.perform(post("/api/admin/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        EmployeeDto resultEmployeeDto = objectMapper.readValue(json, EmployeeDto.class);

        assertThat(resultEmployeeDto, notNullValue());
        assertThat(resultEmployeeDto.getId(), is(1L));
        assertThat(resultEmployeeDto.getName(), is(employee.getName()));
        assertThat(resultEmployeeDto.getLastName(), is(employee.getLastName()));
        assertThat(resultEmployeeDto.getPesel(), is(employee.getPesel()));
    }

    @Test
    public void shouldReturnEmployeeWhenExists() throws Exception{
        Employee employee = createEmployee();
        employee.setId(1L);
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(employee));

        MvcResult result = mockMvc.perform(get("/api/admin/employees/{employeeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        EmployeeDto resultEmployeeDto = objectMapper.readValue(json, EmployeeDto.class);
        Employee resultEmployee = employeeMapper.mapFrom(resultEmployeeDto);

        assertThat(resultEmployeeDto, notNullValue());
        assertThat(resultEmployee, equalTo(employee));
    }

    @Test
    public void shouldReturnNotFoundWhenEmployeeNotExists() throws Exception {
        mockMvc.perform(get("/api/admin/employees/{employeeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound());
    }

    @Test
    public void shouldRemoveEmployeeSuccessfulIfExists() throws Exception {
        Employee employee = createEmployee();
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(employee));

        mockMvc.perform(delete("/api/admin/employees/{employeeId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundWhenDeletingNonExistingEmployee() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/admin/employees/{employeeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdateEmployeeSuccessfulIfExists() throws Exception {
        Employee employee = createEmployee();
        EmployeeDto employeeDto = createEmployeeDto();
        String content = objectMapper.writeValueAsString(employeeDto);

        when(employeeService.updateEmployee(1L, employeeDto)).thenReturn(employee);

        mockMvc.perform(put("/api/admin/employees/{employeeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
    }
}
