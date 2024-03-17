package fr.tao.bankaccount.api;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.customer.CustomerDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.enums.OperationType;
import fr.tao.bankaccount.service.CustomerService;
import fr.tao.bankaccount.utils.CommonTestSetup;

/**
 * Unit tests for the {@link CustomerRestAPI} class using Mockito and MockMvc.
 * Extends {@link CommonTestSetup} for common test setup configuration.
 * 
 * @see CustomerRestAPI
 * @see CommonTestSetup
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@ExtendWith(MockitoExtension.class)
class CustomerRestAPITest extends CommonTestSetup {

	/**
	 * Mocked instance of {@link CustomerService} for testing.
	 */
	@Mock
	private CustomerService customerService;

	/**
	 * Injects mocked dependencies into the {@link CustomerRestAPI} instance.
	 */
	@InjectMocks
	private CustomerRestAPI customerRestAPI;

	/**
	 * MockMvc instance for performing HTTP requests and validating results.
	 */
	private MockMvc mockMvc;

	/**
	 * Set up the test environment before each test method execution.
	 */
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(customerRestAPI).build();
	}

	/**
	 * Test for verifying if the {@link CustomerRestAPI#getCustomers()} method
	 * returns a list of customers successfully.
	 *
	 * @throws Exception if an error occurs while performing the MVC request
	 */
	@Test
	void getCustomers_ReturnsListOfCustomers() throws Exception {
		// Given
		CustomerDto customer1 = new CustomerDto(1L, "Mohamed", "mohamed@example.com");
		CustomerDto customer2 = new CustomerDto(2L, "Ali", "ali@example.com");
		when(customerService.getAllCustomer()).thenReturn(Arrays.asList(customer1, customer2));

		// When and Then
		mockMvc.perform(get("/api/bank/customers")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(customer1.getId()))
				.andExpect(jsonPath("$[0].name").value(customer1.getName()))
				.andExpect(jsonPath("$[1].id").value(customer2.getId()))
				.andExpect(jsonPath("$[1].name").value(customer2.getName()));
	}

	/**
	 * Test for the {@link CustomerRestAPI#getCustomerById(Long)} method. It should
	 * return the customer by ID when a valid ID is provided.
	 *
	 * @throws Exception if an error occurs during the test.
	 */
	@Test
	void getCustomerById_ReturnsCustomerById() throws Exception {
		// Given
		Long customerId = 1L;
		CustomerDto customer = createSampleCustomerDto();
		when(customerService.getCustomerById(customerId)).thenReturn(customer);

		// When and Then
		mockMvc.perform(get("/api/bank/customers/{id}", customerId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(customerId));
	}

	/**
	 * Tests the {@link CustomerRestAPI#searchCustomersByName(String)} method.
	 * Verifies that the endpoint correctly returns matching customers when
	 * searching by name.
	 *
	 * @throws Exception if an error occurs during the test execution
	 */
	@Test
	void searchCustomersByName_ReturnsMatchingCustomers() throws Exception {
		// Given
		String searchName = "Mohamed";
		CustomerDto customer1 = createSampleCustomerDto();
		CustomerDto customer2 = new CustomerDto(2L, "Mohamed", "moha@example.com");
		List<CustomerDto> matchingCustomers = Arrays.asList(customer1, customer2);

		// Mock the behavior of the customerService when searching by name
		when(customerService.getCustomersByNameContains(searchName)).thenReturn(matchingCustomers);

		// When
		ResultActions result = mockMvc.perform(get("/api/bank/customers/search")
				.param("name", searchName)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		// Then
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", Matchers.hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("Mohamed")))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].email", is("moha@example.com")));

		// Verify that the customerService method was called with the correct parameter
		verify(customerService).getCustomersByNameContains(searchName);
	}

	/**
	 * Verifies that invoking {@link CustomerRestAPI#saveCustomer(CustomerDto)}
	 * creates a new customer.
	 *
	 * @throws Exception if an error occurs during the test.
	 */
	@Test
	void saveCustomer_CreatesNewCustomer() throws Exception {
		// Given
		CustomerDto newCustomerDto = createSampleCustomerDto();

		// Mock the behavior of the customerService.saveCustomer method
		when(customerService.saveCustomer(any(CustomerDto.class))).thenReturn(newCustomerDto);

		// When and Then
		mockMvc.perform(post("/api/bank/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(newCustomerDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is(newCustomerDto.getName())))
				.andExpect(jsonPath("$.email", is(newCustomerDto.getEmail())));

		// Verify that the customerService.saveCustomer method was called once with the
		// correct argument
		verify(customerService, times(1)).saveCustomer(any(CustomerDto.class));
	}

	/**
	 * Verifies that the method
	 * {@link CustomerRestAPI#updateCustomer(Long, CustomerDto)} updates customer
	 * details.
	 * 
	 * @throws Exception if an error occurs during the test.
	 */
	@Test
	void updateCustomer_UpdatesCustomerDetails() throws Exception {
		// Given
		Long customerId = 1L;
		CustomerDto updatedCustomerDto = createUpdatedCustomerDto();

		// Mock the customer service behavior
		when(customerService.updateCustomer(customerId, updatedCustomerDto)).thenReturn(updatedCustomerDto);

		// When and Then
		mockMvc.perform(put("/api/bank/customers/{id}", customerId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(updatedCustomerDto)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Mohamed"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("mohamed@example.com"));
	}

	/**
	 * Verifies that the {@link CustomerRestAPI#deleteCustomer(Long)} method
	 * successfully deletes a customer by ID.
	 *
	 * @throws Exception if an error occurs during the test
	 */
	@Test
	void deleteCustomer_DeletesCustomerById() throws Exception {
		// Mock the service method to do nothing when deleteCustomer is called
		doNothing().when(customerService).deleteCustomer(anyLong());

		// Perform the DELETE request
		mockMvc.perform(delete("/api/bank/customers/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()) // Print the request and response																														
				.andExpect(status().isOk())
				.andExpect(content().string("Customer successfully deleted."));

		// Verify that the deleteCustomer method was called with the correct ID
		verify(customerService, times(1)).deleteCustomer(1L);
	}

	/**
	 * Test the {@link CustomerRestAPI#getAllAccountsByCustomerId(Long)} method.
	 *
	 * @throws Exception if an error occurs during the test.
	 */
	@Test
	void getAllAccountsByCustomerId_ReturnsListOfAccounts() throws Exception {
		// Given
		Long customerId = 1L;
		List<AccountDto> accountDtos = Arrays.asList(
				createSampleAccountDto("sampleAccountID1", new BigDecimal("150.00")),
				createSampleAccountDto("sampleAccountID2", new BigDecimal("350.00")));

		when(customerService.getCustomerById(anyLong())).thenReturn(createSampleCustomerDto());
		when(customerService.getAllAccountsByCustomerId(anyLong())).thenReturn(accountDtos);

		// When & Then
		mockMvc.perform(get("/api/bank/customers/{customerId}/accounts", customerId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", Matchers.hasSize(2)))
				.andExpect(jsonPath("$[0].id", is("sampleAccountID1")))
				.andExpect(jsonPath("$[0].balance", is(Matchers.closeTo(150.0, 0.01))))
				.andExpect(jsonPath("$[1].id", is("sampleAccountID2")))
				.andExpect(jsonPath("$[1].balance", is(Matchers.closeTo(350.0, 0.01))));
	}

	/**
	 * Test for {@link CustomerRestAPI#getCustomerOperations(Long)}. It verifies
	 * that the method returns a list of operations for a specific customer.
	 * 
	 * @throws Exception if an error occurs during the test.
	 */
	@Test
	void getCustomerOperations_ReturnsListOfOperations() throws Exception {
		// Given
		Long customerId = 1L;
		CustomerDto customerDto = createSampleCustomerDto();
		AccountDto accountDto = createSampleAccountDto("sampleAccountId", BigDecimal.valueOf(400.0));
		accountDto.setCustomerDto(customerDto);
		List<OperationDto> operationDtos = Arrays.asList(
				createSampleOperationDto(accountDto, OperationType.DEPOSIT, BigDecimal.valueOf(100.0)),
				createSampleOperationDto(accountDto, OperationType.WITHDRAWAL, BigDecimal.valueOf(50.0))
		);

		IntStream.range(0, operationDtos.size())
			.forEach(index -> operationDtos.get(index).setId((long) index + 1));

		when(customerService.getCustomerById(customerId)).thenReturn(customerDto);
		when(customerService.getOperationsByCustomerId(customerId)).thenReturn(operationDtos);

		// When and Then
		mockMvc.perform(get("/api/bank/customers/{customerId}/operations", customerId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].operationType", is("DEPOSIT")))
				.andExpect(jsonPath("$[0].amount", is(100.0)))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].operationType", is("WITHDRAWAL")))
				.andExpect(jsonPath("$[1].amount", is(50.0)));
	}
}
