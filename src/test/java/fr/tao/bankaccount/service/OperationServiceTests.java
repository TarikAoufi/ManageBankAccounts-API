package fr.tao.bankaccount.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.dto.operation.TransferDto;
import fr.tao.bankaccount.entity.Account;
import fr.tao.bankaccount.entity.Operation;
import fr.tao.bankaccount.enums.OperationType;
import fr.tao.bankaccount.exception.InsufficientBalanceException;
import fr.tao.bankaccount.exception.OperationNotFoundException;
import fr.tao.bankaccount.mapper.AccountMapper;
import fr.tao.bankaccount.mapper.OperationMapper;
import fr.tao.bankaccount.repository.OperationRepository;
import fr.tao.bankaccount.service.validation.ValidationService;
import fr.tao.bankaccount.utils.CommonTestSetup;

/**
 * JUnit 5 test class for the {@link OperationServiceImpl} with Mockito extension.
 * Extends {@link CommonTestSetup} for common test configurations.
 *
 * <p>This class utilizes the MockitoExtension to handle mock and injection management.
 * It tests various functionalities of the {@link OperationServiceImpl} class.
 *
 * @see OperationServiceImpl
 * @see CommonTestSetup
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@ExtendWith(MockitoExtension.class)
class OperationServiceTests extends CommonTestSetup {
	
	/**
	 * Mocked and injected instance of {@link OperationServiceImpl}.
	 */
	@InjectMocks
    private OperationServiceImpl operationService;
	
	/**
     * Mock for the {@link AccountService} used as a dependency in {@link OperationServiceImpl}.
     */
	@Mock
	private AccountService accountService;
	
	/**
     * Mock for the {@link OperationRepository} used as a dependency in {@link OperationServiceImpl}.
     */
	@Mock
	private OperationRepository operationRepository;
	
	/**
     * Mock for the {@link OperationMapper} used as a dependency in {@link OperationServiceImpl}.
     */
	@Mock
	private OperationMapper operationMapper;
	
	/**
     * Mock for the {@link AccountMapper} used as a dependency in {@link OperationServiceImpl}.
     */
	@Mock
	private AccountMapper accountMapper;
	
	/**
     * Mock for the {@link ValidationService} used as a dependency in {@link OperationServiceImpl}.
     */
	@Mock
	private ValidationService validationService;
	
	/**
	 * Sets up the test environment before each test method execution.
	 */
	@BeforeEach
	void setup() {
		operationService = new OperationServiceImpl(accountService, operationRepository, operationMapper, accountMapper,
				validationService);
	}
	
	/**
	 * Tests the deposit method with valid data, expecting a successful return of OperationDto.
	 *
	 * @throws Exception If an unexpected exception occurs.
	 */
	@Test
	void deposit_ValidData_ShouldReturnOperationDto() throws Exception {
		// Given
		String accountId = "sampleAccountId";
		BigDecimal balanceAccount = new BigDecimal("500.00");
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Sample deposit";

		var account = createSampleAccount(accountId, balanceAccount);
		AccountDto accountDto = createSampleAccountDto(accountId, balanceAccount);

		// Mocking behavior for accountService.getAccountById
		when(accountService.getAccountById(accountId)).thenReturn(accountDto);

		// Mocking behavior for operationMapper.toEntity
		Operation operation = createSampleOperation(account, OperationType.DEPOSIT, amount);
		when(operationMapper.toEntity(any(OperationDto.class))).thenReturn(operation);

		// Mocking behavior for accountRepository.save
		when(operationRepository.save(operation)).thenReturn(operation);

		// Mocking behavior for operationMapper.toDto
		OperationDto expectedOperationDto = createSampleOperationDto(accountDto, OperationType.DEPOSIT, amount);
		when(operationMapper.toDto(operation)).thenReturn(expectedOperationDto);

		// When
		OperationDto result = operationService.deposit(accountId, amount, description);

		// Then
		assertNotNull(result);
		assertEquals(expectedOperationDto, result);
		// Ensure that the account balance has been updated correctly
		assertEquals(balanceAccount.add(amount), accountDto.getBalance());
		// Verify that necessary methods were called
		verify(accountService, times(1)).getAccountById(accountId);
		verify(accountService, times(1)).updateAccount(accountId, accountDto);
		verify(operationMapper, times(1)).toEntity(any(OperationDto.class));
		verify(operationRepository, times(1)).save(operation);
		verify(operationMapper, times(1)).toDto(operation);
	}
	
	/**
	 * Tests the withdraw method with valid data, expecting a successful return of OperationDto.
	 *
	 * @throws Exception If an unexpected exception occurs.
	 */
	@Test
	void withdraw_ValidData_ShouldReturnOperationDto() throws Exception {
		// Given
		String accountId = "sampleAccountId";
		BigDecimal balanceAccount = new BigDecimal("500.00");
		BigDecimal amount = new BigDecimal("200.00");
		String description = "Sample withdraw";

		var account = createSampleAccount(accountId, balanceAccount);
		AccountDto accountDto = createSampleAccountDto(accountId, balanceAccount);

		// Mocking behavior for accountService.getAccountById
		when(accountService.getAccountById(accountId)).thenReturn(accountDto);

		// Mocking behavior for operationMapper.toEntity
		Operation operation = createSampleOperation(account, OperationType.WITHDRAWAL, amount);
		when(operationMapper.toEntity(any(OperationDto.class))).thenReturn(operation);

		// Mocking behavior for accountRepository.save
		when(operationRepository.save(operation)).thenReturn(operation);

		// Mocking behavior for operationMapper.toDto
		OperationDto expectedOperationDto = createSampleOperationDto(accountDto, OperationType.WITHDRAWAL, amount);
		when(operationMapper.toDto(operation)).thenReturn(expectedOperationDto);

		// When
		OperationDto result = operationService.withdraw(accountId, amount, description);

		// Then
		assertNotNull(result);
		assertEquals(expectedOperationDto, result);
		// Ensure that the account balance has been updated correctly
		assertEquals(balanceAccount.subtract(amount), accountDto.getBalance());
		// Verify that necessary methods were called
		verify(accountService, times(1)).getAccountById(accountId);
		verify(accountService, times(1)).updateAccount(accountId, accountDto);
		verify(operationMapper, times(1)).toEntity(any(OperationDto.class));
		verify(operationRepository, times(1)).save(operation);
		verify(operationMapper, times(1)).toDto(operation);
	}
	
	/**
	 * Tests the withdraw method when the account has insufficient balance,
	 * expecting an InsufficientBalanceException to be thrown.
	 */
	@Test
	void withdraw_InsufficientBalance_ShouldThrowInsufficientBalanceException() {
		// Given
		String accountId = "sampleAccountId";
		BigDecimal withdrawalAmount = new BigDecimal("150.00");
		String withdrawalDescription = "Withdrawal of 150.00";

		// Create a mock AccountDto with insufficient balancel
		AccountDto accountDto = new AccountDto();
		accountDto.setBalance(new BigDecimal("100.00"));

		// Mocking behavior for accountService.getAccountById
		when(accountService.getAccountById(accountId)).thenReturn(accountDto);

		// When/Then
		assertThrows(InsufficientBalanceException.class,
				() -> operationService.withdraw(accountId, withdrawalAmount, withdrawalDescription));

		// Verify that necessary methods were called
		verify(accountService, times(1)).getAccountById(accountId);
		verify(accountService, times(0)).updateAccount(accountId, accountDto); // the account is not updated
		verify(operationRepository, times(0)).save(any(Operation.class)); // no operation is recorded
		verify(operationMapper, times(0)).toDto(any(Operation.class)); // no operation is mapped
	}
	
	/**
	 * Tests the transfer method with valid data, expecting a successful return of TransferDto.
	 *
	 * @throws Exception If an unexpected exception occurs.
	 */
	@Test
	void transfer_ValidData_ShouldReturnTransferDto() throws Exception {
		// Given
		String sourceAccountId = "sourceAccountId";
		String targetAccountId = "targetAccountId";
		BigDecimal initBalanceSource = new BigDecimal("500.00");
		BigDecimal initBalanceTarget = new BigDecimal("200.00");
		BigDecimal amount = new BigDecimal("77.00");

		Account sourceAccount = createSampleAccount(sourceAccountId, initBalanceSource);
		Account targetAccount = createSampleAccount(targetAccountId, initBalanceTarget);

		AccountDto sourceAccountDto = createSampleAccountDto(sourceAccountId, initBalanceSource);
		AccountDto targetAccountDto = createSampleAccountDto(targetAccountId, initBalanceTarget);

		// Mocking behavior for accountService.getAccountById for both source and target
		// accounts
		when(accountService.getAccountById(sourceAccountId)).thenReturn(sourceAccountDto);
		when(accountService.getAccountById(targetAccountId)).thenReturn(targetAccountDto);

		// Mocking behavior for operationMapper.toEntity
		Operation sourceOperation = createSampleOperation(sourceAccount, OperationType.WITHDRAWAL, amount);
		Operation targetOperation = createSampleOperation(targetAccount, OperationType.DEPOSIT, amount);
		when(operationMapper.toEntity(any(OperationDto.class))).thenReturn(sourceOperation).thenReturn(targetOperation);

		// Mocking behavior for operationRepository.save
		when(operationRepository.save(sourceOperation)).thenReturn(sourceOperation);
		when(operationRepository.save(targetOperation)).thenReturn(targetOperation);

		// Mocking behavior for operationMapper.toDto
		OperationDto expectedSourceOperationDto = createSampleOperationDto(sourceAccountDto, OperationType.WITHDRAWAL,
				amount);
		OperationDto expectedTargetOperationDto = createSampleOperationDto(targetAccountDto, OperationType.DEPOSIT,
				amount);
		when(operationMapper.toDto(sourceOperation)).thenReturn(expectedSourceOperationDto);
		when(operationMapper.toDto(targetOperation)).thenReturn(expectedTargetOperationDto);

		// When
		TransferDto result = operationService.transfer(sourceAccountId, targetAccountId, amount);

		// Then
		assertNotNull(result);
		assertEquals(expectedSourceOperationDto, result.getSourceAccountOperationDto());
		assertEquals(expectedTargetOperationDto, result.getTargetAccountOperationDto());

		// Ensure that the account balances have been updated correctly
		assertEquals(initBalanceSource.subtract(amount), sourceAccountDto.getBalance());
		assertEquals(initBalanceTarget.add(amount), targetAccountDto.getBalance());

		// Verify that necessary methods were called
		verify(accountService, times(2)).getAccountById(anyString());
		verify(accountService, times(2)).updateAccount(anyString(), any(AccountDto.class));
		verify(operationMapper, times(2)).toEntity(any(OperationDto.class));
		verify(operationRepository, times(2)).save(any(Operation.class));
		verify(operationMapper, times(2)).toDto(any(Operation.class));
	}
	
	/**
	 * Tests the deleteOperation method for successful deletion of an operation.
	 *
	 * @throws OperationNotFoundException If the operation to be deleted is not found.
	 */
	@Test
	void deleteOperation_Success() throws OperationNotFoundException {
		// Given
		Long operationId = 1L;
		Account account = new Account();
		Operation operation = new Operation();
		operation.setId(operationId);
		operation.setAccount(account);

		when(operationRepository.findById(operationId)).thenReturn(Optional.of(operation));

		// When
		operationService.deleteOperation(operationId);

		// Then
		verify(operationRepository, times(1)).delete(operation);
	}

}
