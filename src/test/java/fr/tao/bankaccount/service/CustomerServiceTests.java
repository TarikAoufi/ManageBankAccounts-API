package fr.tao.bankaccount.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.tao.bankaccount.dto.customer.CustomerDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.entity.Customer;
import fr.tao.bankaccount.entity.Operation;
import fr.tao.bankaccount.utils.CommonTestSetup;
import jakarta.validation.ConstraintViolationException;

/**
 * Integration tests for the {@link CustomerService} class. Uses the Spring Boot
 * testing framework with mocked dependencies. This class is annotated with
 * {@code @SpringBootTest} to enable Spring Boot features in the test
 * environment. The {@code @ExtendWith(MockitoExtension.class)} annotation is
 * used to integrate Mockito for mocking dependencies. It serves as a suite of
 * tests to verify the behavior of the {@link CustomerService} class.
 *
 * @see CommonTestSetup
 * @see CustomerService
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */

@ExtendWith(MockitoExtension.class)
class CustomerServiceTests extends CommonTestSetup {

	/**
	 * Test to verify the behavior of the {@link CustomerService#getAllCustomer()}
	 * method.
	 * 
	 * <p>
	 * Arranges a list of mock customers, mocks the repository to return them, and
	 * then calls the service method. Finally, it asserts that the result is not
	 * null and has the expected size.
	 */
	@Test
	void testGetAllCustomer() {
		// Arrange
		List<Customer> mockCustomers = Arrays.asList(new Customer(), new Customer());
		when(customerRepository.findAll()).thenReturn(mockCustomers);

		// Act
		List<CustomerDto> result = customerService.getAllCustomer();

		// Assert
		assertNotNull(result);
		assertEquals(mockCustomers.size(), result.size());
	}

	/**
	 * Test to verify the behavior of the
	 * {@link CustomerService#getCustomerById(long)} method.
	 * 
	 * <p>
	 * Arranges a mock customer with a specific ID, mocks the repository to return
	 * it, calls the service method, and then asserts that the result is not null
	 * and has the expected attributes.
	 */
	@Test
	void testGetcustomerById() {
		// Arrange
		long customerId = 1L;
		Customer mockCustomer = createSampleCustomer();

		// Mock the behavior of the repository to return the mock customer
		Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

		// Act
		CustomerDto result = customerService.getCustomerById(customerId);

		// Assert
		assertNotNull(result);
		assertEquals(customerId, result.getId());
		assertEquals("Mohamed", result.getName());
		assertEquals("mohamed@example.com", result.getEmail());
	}

	/**
	 * Test to verify the behavior of the
	 * {@link CustomerService#getCustomersByNameContains(String)} method.
	 * 
	 * <p>
	 * Arranges a list of mock customers with names containing a specified
	 * substring, mocks the repository to return them, and mocks the mapper to
	 * return corresponding DTOs. Calls the service method and asserts that the
	 * result is not null and has the expected size. Also, checks that the mapping
	 * from Customer to CustomerDto has been called the correct number of times.
	 */
	@Test
	void testGetCustomersByNameContains() {
		// Arrange
		String nameToSearch = "Ali";
		List<Customer> mockCustomers = Arrays.asList(new Customer(1L, "Ali", "ali@example.com"),
				new Customer(2L, "Zineb", "zineb@example.com"));
		when(customerRepository.findByNameContainsIgnoreCase(nameToSearch)).thenReturn(mockCustomers);

		List<CustomerDto> mockCustomerDtos = Arrays.asList(new CustomerDto(1L, "Ali", "ali@example.com"),
				new CustomerDto(2L, "Zineb", "zineb@example.com"));
		when(customerMapper.toCustomerDTOList(mockCustomers)).thenReturn(mockCustomerDtos);

		// Act
		List<CustomerDto> result = customerService.getCustomersByNameContains(nameToSearch);

		// Assert
		assertNotNull(result);
		assertEquals(mockCustomers.size(), result.size());
		// Check that the mapping from Customer to CustomerDto has been called
		verify(customerMapper, times(2)).toCustomerDTOList(mockCustomers);
	}

	/**
	 * Test to verify the behavior of the
	 * {@link CustomerService#saveCustomer(CustomerDto)} method with valid data.
	 * 
	 * <p>
	 * Arranges a mock customer DTO, mocks the mapper to return an entity, mocks the
	 * repository to return the same entity, and mocks the mapper to return the DTO.
	 * Calls the service method and asserts that the result matches the expected
	 * DTO. Checks that relevant mock methods have been called with the expected
	 * arguments.
	 */
	@Test
	void saveCustomer_WithValidData_ShouldReturnSavedCustomer() throws Exception {
		// Given
		CustomerDto customerDto = createSampleCustomerDto();
		Customer customer = createSampleCustomer();
		when(customerMapper.toEntity(customerDto)).thenReturn(customer);
		when(customerRepository.save(any(Customer.class))).thenReturn(customer);
		when(customerMapper.toDto(customer)).thenReturn(customerDto);
		doNothing().when(validationService).validateAndThrow(any(Customer.class));

		// When
		CustomerDto savedCustomer = customerService.saveCustomer(customerDto);

		// Then
		/* Check that mock methods have been called with the expected arguments */
		verify(customerMapper, times(1)).toEntity(customerDto);
		verify(validationService, times(1)).validateAndThrow(customer);
		verify(customerRepository, times(1)).save(customerCaptor.capture());
		verify(customerMapper, times(1)).toDto(customer);
		assertEquals(customerDto, savedCustomer);
	}

	/**
	 * Test for saving a customer with invalid data. It should throw a
	 * ConstraintViolationException.
	 * 
	 * @see CustomerService#saveCustomer(CustomerDto)
	 */
	@Test
	void saveCustomer_WithInvalidData_ShouldThrowConstraintViolationException() {
		// Given
		CustomerDto customerDto = new CustomerDto(1L, "John", "invalid_email"); // Invalid email format
		Customer customer = new Customer(1L, "John", "invalid_email", new ArrayList<>());
		when(customerMapper.toEntity(customerDto)).thenReturn(customer);
		doThrow(ConstraintViolationException.class).when(validationService).validateAndThrow(any(Customer.class));

		// When/Then
		assertThrows(ConstraintViolationException.class, () -> customerService.saveCustomer(customerDto));
	}

	/**
	 * Test for updating a customer with valid data. It should return the updated
	 * customer.
	 * 
	 * @see CustomerService#updateCustomer(Long, CustomerDto)
	 */
	@Test
	void updateCustomer_WithValidData_ShouldReturnUpdatedCustomer() {
		// Given
		Long customerId = 1L;
		CustomerDto updatedCustomerDto = new CustomerDto(customerId, "Updated", "updated@example.com");
		Customer existingCustomer = new Customer(customerId, "Original", "original@example.com");

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
		doNothing().when(validationService).validateAndThrow(existingCustomer);
		when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);

		// When
		CustomerDto updatedCustomer = customerService.updateCustomer(customerId, updatedCustomerDto);

		// Then
		assertEquals(updatedCustomerDto, updatedCustomer);
		verify(customerRepository, times(1)).findById(customerId);
		verify(customerMapper, times(1)).updateCustomerFromDTO(updatedCustomerDto, existingCustomer);
		verify(validationService, times(1)).validateAndThrow(existingCustomer);
		verify(customerRepository, times(1)).save(existingCustomer);
	}

	/**
	 * Test for updating a customer with invalid data. It should throw a
	 * ConstraintViolationException.
	 * 
	 * @see CustomerService#updateCustomer(Long, CustomerDto)
	 */
	@Test
	void updateCustomer_WithInvalidData_ShouldThrowConstraintViolationException() {
		// Given
		long customerId = 1L;
		CustomerDto customerDto = new CustomerDto(1L, "InvalidName123", "invalid_email");
		Customer customer = new Customer(1L, "InvalidName123", "invalid_email", new ArrayList<>());

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		// when(customerMapper.toEntity(customerDto)).thenReturn(customer);
		doThrow(ConstraintViolationException.class).when(validationService).validateAndThrow(any(Customer.class));

		// When/Then
		assertThrows(ConstraintViolationException.class, () -> customerService.updateCustomer(customerId, customerDto));
	}

	/**
	 * Test for deleting a customer with an existing customer ID. It should delete
	 * the customer.
	 * 
	 * @see CustomerService#deleteCustomer(Long)
	 */
	@Test
	void deleteCustomer_ExistingCustomerId_ShouldDeleteCustomer() {
		// Given
		long customerId = 1L;
		Customer customer = new Customer(1L, "Toto", "toto@example.com");
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

		// When
		customerService.deleteCustomer(customerId);

		// Then
		verify(customerRepository).delete(customer);
	}

	/**
	 * Test for getting operations by customer ID with an existing customer ID. It
	 * should return a list of operation DTOs.
	 * 
	 * @see CustomerService#getOperationsByCustomerId(Long)
	 */
	@Test
	void getOperationsByCustomerId_ExistingCustomerId_ShouldReturnOperations() {
		// Given
		long customerId = 1L;
		Customer customer = new Customer(1L, "Ali", "ali@example.com", new ArrayList<>());
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

		var operationsList = Arrays.asList(new Operation(), new Operation());
		var expectedOperationDtos = Arrays.asList(new OperationDto(), new OperationDto());

		when(customerRepository.findOperationsByCustomerId(customerId)).thenReturn(operationsList);
		when(operationMapper.mapOperations(operationsList)).thenReturn(expectedOperationDtos);

		// When
		List<OperationDto> operations = customerService.getOperationsByCustomerId(customerId);

		// Then
		assertEquals(2, operations.size());
	}

}
