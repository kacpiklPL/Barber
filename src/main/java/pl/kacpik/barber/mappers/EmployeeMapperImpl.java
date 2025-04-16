package pl.kacpik.barber.mappers;

import org.modelmapper.ModelMapper;
import pl.kacpik.barber.model.Employee;
import pl.kacpik.barber.model.dto.EmployeeDto;

public class EmployeeMapperImpl implements Mapper<Employee, EmployeeDto> {

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public EmployeeDto mapTo(Employee employee) {
        return modelMapper.map(employee, EmployeeDto.class);
    }

    @Override
    public Employee mapFrom(EmployeeDto employeeDto) {
        return modelMapper.map(employeeDto, Employee.class);
    }
}
