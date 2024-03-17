package fr.tao.bankaccount.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.account.AccountHistoryDto;
import fr.tao.bankaccount.dto.account.CurrentAccountDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.entity.Account;
import fr.tao.bankaccount.entity.CurrentAccount;
import fr.tao.bankaccount.entity.Customer;
import fr.tao.bankaccount.entity.Operation;
import fr.tao.bankaccount.enums.OperationType;
import fr.tao.bankaccount.exception.AccountNotFoundException;
import fr.tao.bankaccount.exception.CustomerNotFoundException;
import fr.tao.bankaccount.utils.CommonTestSetup;
import fr.tao.bankaccount.validation.UUIDValidator;

/**
 * Test class for the {@link AccountService} containing unit tests for various
 * methods. Uses the MockitoExtension to simplify the setup of mock objects and
 * annotations. Inherits setup functionality from the {@link CommonTestSetup}
 * class.
 *
 * @see AccountService
 * @see CommonTestSetup
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTests extends CommonTestSetup {

	@Mock
	private UUIDValidator uuidValidator;

	/**
	 * Test the retrieval of all accounts from the service. Should return a list of
	 * AccountDto instances matching the number of accounts in the repository.
	 * Verifies that the findAll method of the repository is called once.
	 *
	 * @see AccountService#getAllAccount()
	 */
	@Test
	void getAllAccount_ShouldReturnAllAccounts() {
		// Given
		List<Account> accounts = List.of(new Account(), new Account());
		when(accountRepository.findAll()).thenReturn(accounts);
		when(accountMapper.mapAccount(any())).thenReturn(new AccountDto());

		// When
		List<AccountDto> result = accountService.getAllAccount();

		// Then
		assertEquals(accounts.size(), result.size());
		verify(accountRepository, times(1)).findAll();
	}

	/**
	 * Test the retrieval of an existing account by its ID from the service. Should
	 * return the Account instance matching the provided account ID. Verifies that
	 * the findById method of the repository is called once.
	 *
	 * @see AccountService#findAccountById(String)
	 */
	@Test
	void findAccountById_ExistingAccountId_ShouldReturnAccount() {
		// Given
		String accountId = "1";
		Account account = new Account();
		when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

		// When
		Account result = accountService.findAccountById(accountId);

		// Then
		assertEquals(account, result);
		verify(accountRepository, times(1)).findById(accountId);
	}

	/**
	 * Test the behavior when attempting to retrieve an account with a non-existent
	 * ID from the service. Should throw an AccountNotFoundException. Verifies that
	 * the findById method of the repository is called once.
	 *
	 * @see AccountService#findAccountById(String)
	 */
	@Test
	void findAccountById_WhenAccountDoesNotExist_ShouldThrowAccountNotFoundException() {
		// Given
		String nonExistentAccountId = "nonExistentId";
		when(accountRepository.findById(nonExistentAccountId)).thenReturn(Optional.empty());

		// When/Then
		assertThrows(AccountNotFoundException.class, () -> accountService.findAccountById(nonExistentAccountId));
		verify(accountRepository, times(1)).findById(nonExistentAccountId);
	}

	/**
	 * Test the creation of an account with valid data using the service. Should
	 * return a CurrentAccountDto representing the newly created account. Verifies
	 * that necessary methods such as findById, mapAccountDto, save, and mapAccount
	 * are called as expected.
	 *
	 * @see AccountService#createAccount(Long, AccountDto, Class)
	 */
	@Test
	void createAccount_WithValidData_ShouldReturnCreatedCurrentAccountDto() {
		// Given
		Long customerId = 1L;
		CurrentAccountDto currentAccountDto = new CurrentAccountDto();
		Customer customer = createSampleCustomer();
		CurrentAccount currentAccount = createSampleCurrentAccount();
		CurrentAccountDto createdCurrentAccountDto = createSampleCurrentAccountDto(currentAccount);

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(accountMapper.mapAccountDto(currentAccountDto)).thenReturn(currentAccount);
		when(accountRepository.save(any(CurrentAccount.class))).thenReturn(currentAccount);
		when(accountMapper.mapAccount(currentAccount)).thenReturn(createdCurrentAccountDto);

		// When
		CurrentAccountDto createdAccount = accountService.createAccount(customerId, currentAccountDto,
				CurrentAccountDto.class);

		// Then
		assertAll("Verify account creation", () -> assertEquals(createdCurrentAccountDto, createdAccount),
				// Verify that necessary methods were called
				() -> verify(customerRepository, times(1)).findById(customerId),
				() -> verify(accountMapper, times(1)).mapAccountDto(currentAccountDto),
				() -> verify(accountRepository, times(1)).save(any(CurrentAccount.class)),
				() -> verify(accountMapper, times(1)).mapAccount(currentAccount));
	}

	/**
	 * Test the behavior when attempting to create an account with invalid data,
	 * specifically when the customer is not found. Should throw a
	 * CustomerNotFoundException. Verifies that the findById method of the
	 * repository is called once and mapAccountDto and save are not called.
	 *
	 * @see AccountService#createAccount(Long, AccountDto, Class)
	 */
	@Test
	void createAccount_WithInvalidData_CustomerNotFound_ShouldThrowCustomerNotFoundException() {
		// Given
		Long customerId = 1L;
		AccountDto accountDto = new AccountDto();
		// Set accountDto properties with invalid data

		when(customerRepository.findById(customerId)).thenReturn(Optional.empty()); // Simulate customer not found

		// When/Then
		assertThrows(CustomerNotFoundException.class,
				() -> accountService.createAccount(customerId, accountDto, AccountDto.class));
		// Assert other verifications
		verify(customerRepository, times(1)).findById(customerId);
		verify(accountMapper, times(0)).mapAccountDto(any()); // Ensure mapAccountDto is not called due to customer not
																// found
		verify(accountRepository, times(0)).save(any()); // Ensure save is not called due to customer not found
	}

	/**
	 * Test the update of an existing account with valid data. Should return an
	 * updated AccountDto representing the modified account. Verifies that necessary
	 * methods such as findById, save, and mapAccount are called as expected.
	 *
	 * @see AccountService#updateAccount(String, AccountDto)
	 */
	@Test
	void updateAccount_WithValidData_ShouldReturnUpdatedAccountDto() {
		// Given
		String accountId = "sampleCurrentAccountId";
		CurrentAccount existingAccount = createSampleCurrentAccount();
		CurrentAccountDto updatedAccountDto = createSampleCurrentAccountDto(existingAccount);

		when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
		when(accountRepository.save(any(CurrentAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
		// Mock the behavior for mapAccount
		when(accountMapper.mapAccount(existingAccount)).thenReturn(updatedAccountDto);

		// When
		AccountDto result = accountService.updateAccount(accountId, updatedAccountDto);

		// Then
		assertAll("Verify account update", () -> assertNotNull(result), () -> assertEquals(updatedAccountDto, result),
				// Verify that necessary methods were called
				() -> verify(accountRepository, times(1)).findById(accountId),
				() -> verify(accountRepository, times(1)).save(any(CurrentAccount.class)),
				() -> verify(accountMapper, times(1)).mapAccount(existingAccount));
	}

	/**
	 * Test the behavior when attempting to update an account with invalid data,
	 * specifically when the account is not found. Should throw an
	 * AccountNotFoundException. Verifies that the findById method of the repository
	 * is called once, and no further interactions occur.
	 *
	 * @see AccountService#updateAccount(String, AccountDto)
	 */
	@Test
	void updateAccount_WithInvalidData_ShouldThrowException() {
		// Given
		String accountId = "invalidAccountId";
		AccountDto updatedAccountDto = new AccountDto();

		when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

		// When/Then
		assertThrows(AccountNotFoundException.class,
				() -> accountService.updateAccount(accountId, updatedAccountDto));

		// Verify that necessary methods were called
		verify(accountRepository, times(1)).findById(accountId);
		verifyNoMoreInteractions(accountMapper, accountRepository);
	}

	/**
	 * Test the deletion of an existing account by its ID from the service. Should
	 * successfully delete the account without throwing any exceptions. Verifies
	 * that the findById and delete methods of the repository are called as
	 * expected.
	 *
	 * @see AccountService#deleteAccount(String)
	 */
	@Test
	void deleteAccount_ExistingAccountId_ShouldDeleteAccount() {
		// Given
		String accountId = "sampleAccountId";
		BigDecimal balanceAccount = new BigDecimal("444.00");
		var account = createSampleAccount(accountId, balanceAccount);

		when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

		// When
		assertDoesNotThrow(() -> accountService.deleteAccount(accountId));

		// Then
		// Verify that the delete method was called with the correct account
		verify(accountRepository, times(1)).delete(account);
	}

	/**
	 * Test the retrieval of operations associated with a valid account ID. Should
	 * return a list of OperationDto instances representing the operations. Verifies
	 * that the findById method of the repository, toDto method of the mapper, and
	 * the list size are as expected.
	 *
	 * @see AccountService#getOperationsByAccountId(String)
	 */
	@Test
	void getOperationsByAccountId_WithValidData_ShouldReturnOperationDtoList() {
		// Given
		String accountId = "sampleAccountId";
		BigDecimal balanceAccount = new BigDecimal("500.00");
		BigDecimal amount = new BigDecimal("100.00");

		// Mock the Account
		Account account = mock(Account.class);
		
	    AccountDto accountDto = createSampleAccountDto(accountId, balanceAccount);

		Operation operation = createSampleOperation(account, OperationType.DEPOSIT, amount);
		OperationDto operationDto = createSampleOperationDto(accountDto, OperationType.DEPOSIT, amount);

		when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
		List<Operation> operationsList = Collections.singletonList(operation);
		when(account.getOperations()).thenReturn(operationsList);
		when(operationMapper.toDto(operation)).thenReturn(operationDto);

		// When
		List<OperationDto> result = accountService.getOperationsByAccountId(accountId);

		// Then
		assertEquals(1, result.size());
		assertEquals(operationDto, result.get(0));
		// Verify that necessary methods were called
		verify(accountRepository, times(1)).findById(accountId);
		verify(operationMapper, times(1)).toDto(operation);
	}

	/**
	 * Test the retrieval of account history by providing a valid account ID, page,
	 * and size. Should return an AccountHistoryDto containing AccountDto and a list
	 * of OperationDto instances. Verifies that the findById,
	 * findOperationsByAccountIdOrderByOperationDateDesc, mapAccount, and toDto
	 * methods are called as expected.
	 *
	 * @see AccountService#accountHistoryByPage(String, int, int)
	 */
	@Test
	void accountHistoryByPage_ValidAccountIdAndPageAndSize_ShouldReturnAccountHistoryDto() {
		// Given
		String accountId = "a1ab37c0-238e-40fd-a85b-f802b60a8c82";
		int page = 1;
		int size = 10;

		// Create a sample account
		Account account = createSampleAccount(accountId, new BigDecimal("222.00"));

		// Create sample operations for the account
		List<Operation> operations = createSampleOperations(account, 5);

		OperationDto operationDto = new OperationDto();
		AccountDto accountDto = new AccountDto();

		// Mock the account retrieval
		when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
		// Mocking accountRepository to return a page with operations
		when(accountRepository.findOperationsByAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size)))
				.thenReturn(new PageImpl<>(operations));
		// Mock mapping methods
		when(accountMapper.mapAccount(account)).thenReturn(accountDto);
		when(operationMapper.toDto(any(Operation.class))).thenReturn(operationDto);

		// When
		AccountHistoryDto accountHistoryDto = accountService.accountHistoryByPage(accountId, page, size);

		// Then
		assertNotNull(accountHistoryDto);
		assertNotNull(accountHistoryDto.getAccountDto());
		assertEquals(operations.size(), accountHistoryDto.getOperationDtos().size());

		// Verify method invocations
		verify(accountRepository, times(1)).findById(accountId);
		verify(accountRepository, times(1)).findOperationsByAccountIdOrderByOperationDateDesc(accountId,
				PageRequest.of(page, size));
		verify(accountMapper, times(1)).mapAccount(account);
		verify(operationMapper, times(operations.size())).toDto(any(Operation.class));
	}

}
