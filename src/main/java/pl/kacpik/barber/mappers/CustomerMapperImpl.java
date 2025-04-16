package pl.kacpik.barber.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.kacpik.barber.model.Customer;
import pl.kacpik.barber.model.dto.CustomerDto;

@Component
public class CustomerMapperImpl implements Mapper<Customer, CustomerDto> {

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public CustomerDto mapTo(Customer customer) {
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public Customer mapFrom(CustomerDto customerDto) {
        return modelMapper.map(customerDto, Customer.class);
    }
}
