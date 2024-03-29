package fr.tao.bankaccount.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import fr.tao.bankaccount.dto.customer.CustomerDto;
import fr.tao.bankaccount.entity.Customer;

/**
 * Mapper interface for converting between different representations of customers.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Mapper(componentModel = "spring", uses = { AccountMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {
	
	/**
     * Instance of the CustomerMapper interface generated by MapStruct.
     */
	CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);
	
	/**
     * Converts a Customer entity to its corresponding DTO representation.
     *
     * @param customer The Customer entity to be converted.
     * @return The corresponding CustomerDto.
     */
	CustomerDto toDto(Customer customer);
	
	/**
     * Converts a CustomerDto to its corresponding entity representation.
     *
     * @param customerDto The CustomerDto to be converted.
     * @return The corresponding Customer entity.
     */
    Customer toEntity(CustomerDto customerDto);
	
    /**
     * Updates an existing Customer entity based on the data in the provided CustomerDto.
     *
     * @param customerDto The data to update the Customer entity.
     * @param customer    The target Customer entity to be updated.
     */
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateCustomerFromDTO(CustomerDto customerDto, @MappingTarget Customer customer);
	
	/**
     * Converts a list of Customer entities to a list of corresponding CustomerDto objects.
     *
     * @param customers The list of Customer entities to be converted.
     * @return The list of corresponding CustomerDto objects.
     */
    List<CustomerDto> toCustomerDTOList(List<Customer> customers);

}
