package fr.tao.bankaccount.utils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.account.CurrentAccountDto;
import fr.tao.bankaccount.dto.account.SavingsAccountDto;
import fr.tao.bankaccount.dto.customer.CustomerDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.entity.Account;
import fr.tao.bankaccount.entity.CurrentAccount;
import fr.tao.bankaccount.entity.Customer;
import fr.tao.bankaccount.entity.Operation;
import fr.tao.bankaccount.enums.AccountStatus;
import fr.tao.bankaccount.enums.OperationType;
import fr.tao.bankaccount.mapper.AccountMapper;
import fr.tao.bankaccount.mapper.CustomerMapper;
import fr.tao.bankaccount.mapper.OperationMapper;
import fr.tao.bankaccount.repository.AccountRepository;
import fr.tao.bankaccount.repository.CustomerRepository;
import fr.tao.bankaccount.repository.OperationRepository;
import fr.tao.bankaccount.service.AccountServiceImpl;
import fr.tao.bankaccount.service.CustomerServiceImpl;
import fr.tao.bankaccount.service.validation.ValidationService;

/**
 * Common setup for test classes, providing mocked dependencies and utility
 * methods. This class is abstract and serves as a base for test classes,
 * containing common configurations and sample data creation methods. It uses
 * Mockito annotations for dependency injection and mocking.
 *
 * @see CustomerServiceTests
 * @see AccountServiceTests
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
public abstract class CommonTestSetup {

	/**
	 * Mocked and injected instance of {@link AccountServiceImpl}.
	 */
	@InjectMocks
	protected AccountServiceImpl accountService;

	/**
	 * Mocked and injected instance of {@link CustomerServiceImpl}.
	 */
	@InjectMocks
	protected CustomerServiceImpl customerService;

	@Mock
	protected OperationRepository operationRepository;

	/**
	 * Mock of {@link AccountRepository}.
	 */
	@Mock
	protected AccountRepository accountRepository;

	/**
	 * Mock of {@link CustomerRepository}.
	 */
	@Mock
	protected CustomerRepository customerRepository;

	/**
	 * Mock of {@link AccountMapper}.
	 */
	@Mock
	protected AccountMapper accountMapper;

	/**
	 * Mock of {@link OperationMapper}.
	 */
	@Mock
	protected OperationMapper operationMapper;

	/**
	 * Spied instance of {@link CustomerMapper}.
	 */
	@Spy
	protected CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

	/**
	 * Mock of {@link ValidationService}.
	 */
	@Mock
	protected ValidationService validationService;

	/**
	 * Argument captor for {@link Account} used for capturing method arguments
	 * during testing.
	 */
	@Captor
	protected ArgumentCaptor<Account> accountCaptor;

	/**
	 * Argument captor for {@link Customer} used for capturing method arguments
	 * during testing.
	 */
	@Captor
	protected ArgumentCaptor<Customer> customerCaptor;

	/**
	 * Create and return a sample Customer entity.
	 *
	 * @return A sample {@link Customer} entity.
	 */
	protected Customer createSampleCustomer() {
		return new Customer(1L, "Mohamed", "mohamed@example.com");
	}

	/**
	 * Create and return a sample Customer DTO.
	 *
	 * @return A sample {@link CustomerDto}.
	 */
	protected CustomerDto createSampleCustomerDto() {
		return new CustomerDto(1L, "Mohamed", "mohamed@example.com");
	}

	/**
	 * Creates a sample {@link Account} instance with the given account ID and
	 * balance.
	 *
	 * @param accountId The ID for the account.
	 * @param balance   The balance for the account.
	 * @return A newly created {@link Account} instance.
	 */
	protected Account createSampleAccount(String accountId, BigDecimal balance) {
		Account account = new Account();
		account.setId(accountId);
		account.setBalance(balance);
		return account;
	}

	/**
	 * Creates a sample {@link AccountDto} instance with the given account ID and
	 * balance.
	 *
	 * @param accountId The ID for the account.
	 * @param balance   The balance for the account.
	 * @return A newly created {@link AccountDto} instance.
	 */
	protected AccountDto createSampleAccountDto(String accountId, BigDecimal balance) {
		AccountDto accountDto = new AccountDto();
		accountDto.setId(accountId);
		accountDto.setBalance(balance);
		return accountDto;
	}

	/**
	 * Creates a sample {@link OperationDto} instance with the given
	 * {@link AccountDto}, operation type, and amount.
	 *
	 * @param accountDto    The {@link AccountDto} associated with the operation.
	 * @param operationType The type of operation.
	 * @param amount        The amount involved in the operation.
	 * @return A newly created {@link OperationDto} instance.
	 */
	protected OperationDto createSampleOperationDto(AccountDto accountDto, OperationType operationType,
			BigDecimal amount) {
		OperationDto operationDto = new OperationDto();
		operationDto.setAccountDto(accountDto);
		operationDto.setOperationType(operationType);
		operationDto.setAmount(amount);
		return operationDto;
	}

	/**
	 * Creates a sample {@link Operation} instance with the given {@link Account},
	 * operation type, and amount.
	 *
	 * @param account       The {@link Account} associated with the operation.
	 * @param operationType The type of operation.
	 * @param amount        The amount involved in the operation.
	 * @return A newly created {@link Operation} instance.
	 */
	protected Operation createSampleOperation(Account account, OperationType operationType, BigDecimal amount) {
		Operation operation = new Operation();
		operation.setAccount(account);
		operation.setOperationType(operationType);
		operation.setAmount(amount);
		return operation;
	}

	/**
	 * Creates a sample {@link CurrentAccount} instance with default values.
	 *
	 * @return A newly created {@link CurrentAccount} instance.
	 */
	protected CurrentAccount createSampleCurrentAccount() {
		Customer customer = createSampleCustomer();
		CurrentAccount currentAccount = new CurrentAccount();
		currentAccount.setId("sampleAccountId");
		currentAccount.setBalance(new BigDecimal("1500.00"));
		currentAccount.setCreatedOn(ZonedDateTime.now());
		currentAccount.setStatus(AccountStatus.CREATED);
		currentAccount.setModifiedOn(ZonedDateTime.now());
		currentAccount.setCustomer(customer);
		currentAccount.setOverdraftLimit(new BigDecimal("500.00"));
		return currentAccount;
	}

	/**
	 * Creates a sample {@link CurrentAccountDto} instance based on the provided
	 * {@link CurrentAccount}.
	 *
	 * @param currentAccount The {@link CurrentAccount} for which to create a
	 *                       {@link CurrentAccountDto}.
	 * @return A newly created {@link CurrentAccountDto} instance.
	 */
	protected CurrentAccountDto createSampleCurrentAccountDto(CurrentAccount currentAccount) {
		CustomerDto customerDto = createSampleCustomerDto();
		CurrentAccountDto currentAccountDto = new CurrentAccountDto();
		currentAccountDto.setId(currentAccount.getId());
		currentAccountDto.setBalance(currentAccount.getBalance());
		currentAccountDto.setCreatedOn(currentAccount.getCreatedOn());
		currentAccountDto.setStatus(currentAccount.getStatus());
		currentAccountDto.setModifiedOn(currentAccount.getModifiedOn());
		currentAccountDto.setCustomerDto(customerDto);
		currentAccountDto.setOverdraftLimit(currentAccount.getOverdraftLimit());
		return currentAccountDto;
	}
	
	/**
	 * Creates a sample {@link CurrentAccountDto} for testing purposes.
	 *
	 * @return A sample CurrentAccountDto with predefined values.
	 * @see CustomerDto
	 * @see CurrentAccountDto
	 */
	protected CurrentAccountDto createSampleCurrentAccountDto() {
		CustomerDto customerDto = createSampleCustomerDto();
		CurrentAccountDto currentAccountDto = new CurrentAccountDto();
        currentAccountDto.setId("sampleAccountId");
        currentAccountDto.setBalance(new BigDecimal("900.00"));
        currentAccountDto.setOverdraftLimit(new BigDecimal("300.00"));
        currentAccountDto.setCustomerDto(customerDto);
		return currentAccountDto;
	}
	
	/**
	 * Creates an updated version of {@link CurrentAccountDto} for testing purposes.
	 *
	 * @param accountId The ID of the account to be updated.
	 * @return An updated CurrentAccountDto with modified values.
	 * @see CustomerDto
	 * @see CurrentAccountDto
	 */
	protected CurrentAccountDto updatedCurrentAccountDto(String accountId) {
		CustomerDto customerDto = createSampleCustomerDto();
		CurrentAccountDto updatedCurrentAccountDto = new CurrentAccountDto();
		updatedCurrentAccountDto.setId(accountId);
		updatedCurrentAccountDto.setBalance(new BigDecimal("3500.00"));
		updatedCurrentAccountDto.setOverdraftLimit(new BigDecimal("800.00"));
		updatedCurrentAccountDto.setCustomerDto(customerDto);
		return updatedCurrentAccountDto;
	}
	
	/**
	 * Creates a sample SavingsAccountDto for testing purposes.
	 *
	 * @return A sample SavingsAccountDto with predefined values.
	 * @see SavingsAccountDto
	 * @see CustomerDto
	 */
	protected SavingsAccountDto createSampleSavingsAccountDto() {
		CustomerDto customerDto = createSampleCustomerDto();
		SavingsAccountDto savingsAccountDto = new SavingsAccountDto();
		savingsAccountDto.setId("sampleAccountId");
		savingsAccountDto.setBalance(new BigDecimal("1200.00"));
		savingsAccountDto.setInterestRate(new BigDecimal("5.55"));
		savingsAccountDto.setCustomerDto(customerDto);
		return savingsAccountDto;
	}
	
	/**
	 * Creates an updated SavingsAccountDto for testing purposes.
	 *
	 * @param accountId The ID of the savings account to be updated.
	 * @return An updated SavingsAccountDto with predefined values.
	 * @see SavingsAccountDto
	 * @see CustomerDto
	 */
	protected SavingsAccountDto updatedSavingsAccountDto(String accountId) {
		CustomerDto customerDto = createSampleCustomerDto();
		SavingsAccountDto updatedSavingsAccountDto = new SavingsAccountDto();
		updatedSavingsAccountDto.setId(accountId);
		updatedSavingsAccountDto.setBalance(new BigDecimal("666.06"));
		updatedSavingsAccountDto.setInterestRate(new BigDecimal("8.88"));
		updatedSavingsAccountDto.setCustomerDto(customerDto);
		return updatedSavingsAccountDto;
	}
	
	/**
	 * Creates a list of sample {@link Operation} instances associated with the
	 * given {@link Account}.
	 *
	 * @param account The {@link Account} to associate with the operations.
	 * @param count   The number of operations to create in the list.
	 * @return A list containing the newly created {@link Operation} instances.
	 */
	protected List<Operation> createSampleOperations(Account account, int count) {
		List<Operation> operations = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			Operation operation = new Operation();
			// Populate operation fields
			operation.setAccount(account);
			operations.add(operation);
		}
		return operations;
	}

	/**
	 * Convert an object to its JSON representation.
	 *
	 * @param obj the object to convert.
	 * @return the JSON representation of the object.
	 * @throws JsonProcessingException if an error occurs during JSON processing.
	 */
	protected String asJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	/**
	 * Helper method to create an example of an updated customer DTO
	 * 
	 * @return A sample {@link CustomerDto}.
	 */
	protected CustomerDto createUpdatedCustomerDto() {
		CustomerDto updatedCustomerDto = new CustomerDto();
		updatedCustomerDto.setId(1L);
		updatedCustomerDto.setName("Mohamed");
		updatedCustomerDto.setEmail("mohamed@example.com");
		return updatedCustomerDto;
	}

}