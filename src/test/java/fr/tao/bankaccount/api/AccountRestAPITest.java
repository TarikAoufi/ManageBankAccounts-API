package fr.tao.bankaccount.api;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.tao.bankaccount.dto.account.AccountDetailsDto;
import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.account.AccountHistoryDto;
import fr.tao.bankaccount.dto.account.CurrentAccountDto;
import fr.tao.bankaccount.dto.account.SavingsAccountDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.enums.OperationType;
import fr.tao.bankaccount.service.AccountService;
import fr.tao.bankaccount.utils.CommonTestSetup;

/**
 * Unit tests for the {@link AccountRestAPI} class using JUnit 5 and Mockito.
 * Extends {@link CommonTestSetup} for common test setup configurations.
 *
 * @see AccountRestAPI
 * @see CommonTestSetup
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@ExtendWith(MockitoExtension.class)
class AccountRestAPITest extends CommonTestSetup {

	/**
	 * The instance of {@link AccountRestAPI} to be tested, injected with mock
	 * dependencies.
	 */
	@InjectMocks
	private AccountRestAPI accountRestAPI;

	/**
	 * Mock instance of {@link AccountService} for testing.
	 */
	@Mock
	private AccountService accountService;

	/**
	 * MockMvc instance for performing HTTP requests.
	 */
	private MockMvc mockMvc;

	/**
	 * Set up the test environment before each test method execution.
	 */
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(accountRestAPI).build();
	}

	/**
	 * Test method to verify that {@link AccountRestAPI#getAccounts()} returns a
	 * list of accounts.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 * @see AccountRestAPI#getAccounts()
	 */
	@Test
	void getAccounts_ReturnsListOfAccounts() throws Exception {
		// Mocking behavior
		List<AccountDto> accounts = Arrays.asList(new AccountDto(), new AccountDto());
		Mockito.when(accountService.getAllAccount()).thenReturn(accounts);

		// Perform the request
		mockMvc.perform(get("/api/bank/accounts")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(2)));
	}

	/**
	 * Test method to verify that {@link AccountRestAPI#getAccountById(String)}
	 * returns the expected account by ID.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 * @see AccountRestAPI#getAccountById(String)
	 */
	@Test
	void getAccountById_ReturnsAccountById() throws Exception {
		// Mocking behavior
		String accountId = "sampleAccountId";
		AccountDto account = createSampleAccountDto(accountId, new BigDecimal("999.00"));
		Mockito.when(accountService.getAccountById(accountId)).thenReturn(account);

		// Perform the request
		mockMvc.perform(get("/api/bank/accounts/" + accountId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is("sampleAccountId")))
				.andExpect(jsonPath("$.balance", is(Matchers.closeTo(999.0, 0.01))));
	}

	/**
	 * Test method to verify that {@link AccountRestAPI#getAllAccountDetails()}
	 * returns a list of all account details.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 * @see AccountRestAPI#getAllAccountDetails()
	 */
	@Test
	void getAllAccountDetails_ReturnsAllAccountDetails() throws Exception {
		// Mocking behavior
		var accountDetailsDtos = Arrays.asList(new AccountDetailsDto(), new AccountDetailsDto());
		Mockito.when(accountService.getAllAccountDetails()).thenReturn(accountDetailsDtos);

		// Perform the request
		mockMvc.perform(get("/api/bank/accounts/accounts-details")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(2)));
	}
	
	/**
	 * Test method to verify that
	 * {@link AccountRestAPI#getAccountDetailsById(String)} returns account details
	 * by ID.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 * @see AccountRestAPI#getAccountDetailsById(String)
	 */
	@Test
	void getAccountDetailsById_ReturnsAccountDetailsById() throws Exception {
		// Mocking behavior
		String accountId = "sampleAccountId";
		var accountDto = createSampleAccountDto(accountId, new BigDecimal("77.00"));
		var operationDto = createSampleOperationDto(accountDto, OperationType.WITHDRAWAL, new BigDecimal("555.00"));
		AccountDetailsDto accountDetailsDto = new AccountDetailsDto(accountDto, Arrays.asList(operationDto));
		Mockito.when(accountService.getAccountDetailsById(accountId)).thenReturn(accountDetailsDto);

		// Perform the request
		mockMvc.perform(get("/api/bank/accounts/" + accountId + "/accounts-details")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accountDto.balance", is(Matchers.closeTo(77.0, 0.01))))
				.andExpect(jsonPath("$.operationDtos", Matchers.hasSize(1)))
				.andExpect(jsonPath("$.operationDtos[0].amount", is(555.0)));
	}
	
	/**
	 * Test method to verify that
	 * {@link AccountRestAPI#createCurrentAccount(Long, CurrentAccountDto)}
	 * successfully creates a Current Account.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 * @see AccountRestAPI#createCurrentAccount(Long, CurrentAccountDto)
	 */
	@Test
	void createCurrentAccount_CreatesCurrentAccount() throws Exception {
		// Mocking behavior
		Long customerId = 1L;
		String accountId = "createdAccountId123";

		// Create a sample CurrentAccountDto for the request
		CurrentAccountDto currentAccountDto = createSampleCurrentAccountDto();

		// Create a corresponding created CurrentAccountDto for the response
		CurrentAccountDto createdCurrentAccountDto = updatedCurrentAccountDto(accountId);

		Mockito.when(accountService.createAccount(customerId, currentAccountDto, CurrentAccountDto.class))
				.thenReturn(createdCurrentAccountDto);

		// Perform the request
		mockMvc.perform(post("/api/bank/accounts/current/" + customerId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(currentAccountDto)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("createdAccountId123"))
				.andExpect(jsonPath("$.balance").value(3500.00)).andExpect(jsonPath("$.overdraftLimit").value(800.00))
				.andExpect(jsonPath("$.customerDto.id").value(1L))
				.andExpect(jsonPath("$.customerDto.name").value("Mohamed"))
				.andExpect(jsonPath("$.customerDto.email").value("mohamed@example.com"));
	}
	
	/**
	 * Test method to verify that
	 * {@link AccountRestAPI#createSavingsAccount(Long, SavingsAccountDto)}
	 * successfully creates a Savings Account.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 * @see AccountRestAPI#createSavingsAccount(Long, SavingsAccountDto)
	 */
	@Test
	void createSavingsAccount_CreatesSavingsAccount() throws Exception {
		// Mocking behavior
		Long customerId = 1L;
		String accountId = "generatedAccountId";

		// Create a sample SavingsAccountDto for the request
		SavingsAccountDto savingsAccountDto = createSampleSavingsAccountDto();

		// Create a corresponding created SavingsAccountDto for the response
		SavingsAccountDto createdSavingsAccountDto = updatedSavingsAccountDto(accountId);

		Mockito.when(accountService.createAccount(customerId, savingsAccountDto, SavingsAccountDto.class))
				.thenReturn(createdSavingsAccountDto);

		// Perform the request
		mockMvc.perform(post("/api/bank/accounts/savings/" + customerId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(savingsAccountDto)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("generatedAccountId")).andExpect(jsonPath("$.balance").value(666.06))
				.andExpect(jsonPath("$.interestRate").value(8.88)).andExpect(jsonPath("$.customerDto.id").value(1L))
				.andExpect(jsonPath("$.customerDto.name").value("Mohamed"))
				.andExpect(jsonPath("$.customerDto.email").value("mohamed@example.com"));
	}
	
	/**
	 * Test method to verify that
	 * {@link AccountRestAPI#updateCurrentAccount(String, CurrentAccountDto)}
	 * successfully updates a Current Account.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 * @see AccountRestAPI#updateCurrentAccount(String, CurrentAccountDto)
	 */
	@Test
	void updateCurrentAccount_UpdatesCurrentAccount() throws Exception {
		// Mocking behavior
		String accountId = "sampleAccountId";

		// Create a sample CurrentAccountDto for the request
		CurrentAccountDto currentAccountDto = createSampleCurrentAccountDto();

		// Create a sample updated CurrentAccountDto for the response
		CurrentAccountDto updatedCurrentAccountDto = updatedCurrentAccountDto(accountId);

		Mockito.when(accountService.updateAccount(accountId, currentAccountDto)).thenReturn(updatedCurrentAccountDto);

		// Perform the request
		mockMvc.perform(put("/api/bank/accounts/current/" + accountId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(currentAccountDto)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("sampleAccountId"))
				.andExpect(jsonPath("$.balance").value(3500.00))
				.andExpect(jsonPath("$.overdraftLimit").value(800.00))
				.andExpect(jsonPath("$.customerDto.id").value(1L))
				.andExpect(jsonPath("$.customerDto.name").value("Mohamed"))
				.andExpect(jsonPath("$.customerDto.email").value("mohamed@example.com"));
	}
	
	/**
	 * Test method to verify that
	 * {@link AccountRestAPI#updateSavingsAccount(String, SavingsAccountDto)}
	 * successfully updates a Savings Account.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 * @see AccountRestAPI#updateSavingsAccount(String, SavingsAccountDto)
	 */
	@Test
	void updateSavingsAccount_UpdatesSavingsAccount() throws Exception {
		// Mocking behavior
		String accountId = "sampleAccountId";

		// Create a sample SavingsAccountDto for the request
		SavingsAccountDto savingsAccountDto = createSampleSavingsAccountDto();

		// Create a sample updated SavingsAccountDto for the response
		SavingsAccountDto updatedSavingsAccountDto = updatedSavingsAccountDto(accountId);

		Mockito.when(accountService.updateAccount(accountId, savingsAccountDto)).thenReturn(updatedSavingsAccountDto);

		// Perform the request
		mockMvc.perform(put("/api/bank/accounts/savings/" + accountId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(savingsAccountDto)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("sampleAccountId"))
				.andExpect(jsonPath("$.balance").value(666.06))
				.andExpect(jsonPath("$.interestRate").value(8.88))
				.andExpect(jsonPath("$.customerDto.id").value(1L))
				.andExpect(jsonPath("$.customerDto.name").value("Mohamed"))
				.andExpect(jsonPath("$.customerDto.email").value("mohamed@example.com"));
	}
	
	/**
	 * Test method to verify that {@link AccountRestAPI#deleteAccount(String)}
	 * successfully deletes an account.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 * @see AccountRestAPI#deleteAccount(String)
	 */
	@Test
	void deleteAccount_DeletesAccount() throws Exception {
		// Mocking behavior
		String accountId = "sampleAccountId";

		// Perform the request
		mockMvc.perform(delete("/api/bank/accounts/" + accountId))
			.andExpect(status().isOk());
	}
	
	/**
	 * Test method to verify that
	 * {@link AccountRestAPI#getAccountOperations(String)} successfully returns
	 * account operations.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 * @see AccountRestAPI#getAccountOperations(String)
	 */
	@Test
	void getAccountOperations_ReturnsAccountOperations() throws Exception {
		// Mocking behavior
		String accountId = "sampleAccountId";
		
		// Create a sample account DTO
		AccountDto accountDto = createSampleAccountDto(accountId, BigDecimal.valueOf(400.0));
		
		// Create sample operation list
		List<OperationDto> operationDtos = Arrays.asList(
				createSampleOperationDto(accountDto, OperationType.DEPOSIT, BigDecimal.valueOf(100.0)),
				createSampleOperationDto(accountDto, OperationType.WITHDRAWAL, BigDecimal.valueOf(50.0)));

		// Set IDs for each operation
		IntStream.range(0, operationDtos.size())
			.forEach(index -> operationDtos.get(index).setId((long) index + 1));
		
		// Mock the service method and validate the response
		Mockito.when(accountService.getOperationsByAccountId(accountId)).thenReturn(operationDtos);

		// Perform the request and validate the response
		mockMvc.perform(get("/api/bank/accounts/" + accountId + "/operations"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].operationType", is(OperationType.DEPOSIT.toString())))
				.andExpect(jsonPath("$[0].amount", is(100.0)))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].operationType", is(OperationType.WITHDRAWAL.toString())))
				.andExpect(jsonPath("$[1].amount", is(50.0)));
	}
	
	/**
	 * Tests the {@link AccountRestAPI#getAccountHistoryByPage(String, int, int)}
	 * method. Verifies that the method returns the expected account history by
	 * page.
	 *
	 * @throws Exception if an error occurs during the test execution
	 * @see AccountRestAPI#getAccountHistoryByPage(String, int, int)
	 */
	@Test
	void getAccountHistoryByPage_ReturnsAccountHistoryByPage() throws Exception {
		// Mocking behavior
		String accountId = "sampleAccountId";
		int page = 0;
		int size = 5;

		// Sample account data
		AccountDto accountDto = createSampleAccountDto(accountId, BigDecimal.valueOf(5333.0));
		List<OperationDto> operationDtos = Arrays.asList(
				createSampleOperationDto(accountDto, OperationType.DEPOSIT, BigDecimal.valueOf(200.0)),
				createSampleOperationDto(accountDto, OperationType.WITHDRAWAL, BigDecimal.valueOf(50.0)),
				createSampleOperationDto(accountDto, OperationType.DEPOSIT, BigDecimal.valueOf(300.0)),
				createSampleOperationDto(accountDto, OperationType.WITHDRAWAL, BigDecimal.valueOf(100.0)),
				createSampleOperationDto(accountDto, OperationType.DEPOSIT, BigDecimal.valueOf(150.0)));

		IntStream.range(0, operationDtos.size())
			.forEach(index -> operationDtos.get(index).setId((long) index + 1));

		// Create AccountHistoryDto
		AccountHistoryDto accountHistoryDto = new AccountHistoryDto();
		accountHistoryDto.setAccountDto(accountDto);
		accountHistoryDto.setOperationDtos(operationDtos);

		// Mock the service response
		Mockito.when(accountService.accountHistoryByPage(accountId, page, size)).thenReturn(accountHistoryDto);

		// Perform the request
		mockMvc.perform(get("/api/bank/accounts/" + accountId + "/history")
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.accountDto.balance").value(5333.0))
				.andExpect(jsonPath("$.operationDtos[2].operationType", is("DEPOSIT")))
				.andExpect(jsonPath("$.operationDtos[2].amount", is(300.0)))
				.andExpect(jsonPath("$.operationDtos[3].id", is(4)))
				.andExpect(jsonPath("$.operationDtos[3].operationType", is("WITHDRAWAL")))
				.andExpect(jsonPath("$.operationDtos[3].amount", is(100.0)));
	}

}
