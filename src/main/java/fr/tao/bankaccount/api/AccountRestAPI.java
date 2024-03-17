package fr.tao.bankaccount.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.tao.bankaccount.dto.account.AccountDetailsDto;
import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.account.AccountHistoryDto;
import fr.tao.bankaccount.dto.account.CurrentAccountDto;
import fr.tao.bankaccount.dto.account.SavingsAccountDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.exception.AccountNotFoundException;
import fr.tao.bankaccount.exception.CustomerNotFoundException;
import fr.tao.bankaccount.service.AccountService;
import fr.tao.bankaccount.util.MessageUtil;
import jakarta.persistence.NoResultException;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * RESTful API controller for managing bank accounts.
 *
 * Handles operations related to retrieving, creating, updating, and deleting bank accounts.
 * Provides endpoints for various account-related actions such as getting account details,
 * creating current and savings accounts, updating account information, and more.
 *
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@RestController
@CrossOrigin("*")
@RequestMapping(produces = "application/json", value = "/api/bank/accounts")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AccountRestAPI {
	
	@NonNull private final AccountService accountService;
	
	/**
     * Retrieve all accounts.
     *
     * @return ResponseEntity containing a list of AccountDto.
     * @throws NoResultException if no accounts are found.
     */
	@GetMapping
	public ResponseEntity<List<AccountDto>> getAccounts() throws NoResultException {
		log.info("Invoking getAccounts - AccountRestAPI");
		var accountDtos = accountService.getAllAccount();
		return ResponseEntity.ok().body(accountDtos);
	}
	
	/**
     * Retrieve account by ID.
     *
     * @param accountId The ID of the account.
     * @return ResponseEntity containing the AccountDto.
     * @throws AccountNotFoundException if the account with the specified ID is not found.
     */
	@GetMapping("/{accountId}")
	public ResponseEntity<AccountDto> getAccountById(@PathVariable String accountId) throws AccountNotFoundException {
		log.info("Call getAccountById - AccountRestAPI");
		var accountDto = accountService.getAccountById(accountId);
		return ResponseEntity.ok().body(accountDto);
	}
	
	/**
	 * Retrieve details of all accounts.
	 *
	 * @return ResponseEntity containing a list of AccountDetailsDto.
	 * @throws NoResultException if no account details are found.
	 */
	@GetMapping("/accounts-details")
	public ResponseEntity<List<AccountDetailsDto>> getAllAccountDetails() throws NoResultException {
		log.info("Invoking getAllAccountDetails - AccountRestAPI");
		var accountDetailsDtos = accountService.getAllAccountDetails();
		return new ResponseEntity<>(accountDetailsDtos, HttpStatus.OK);

	}
	
	/**
	 * Retrieve details of an account by ID.
	 *
	 * @param accountId The ID of the account.
	 * @return ResponseEntity containing the AccountDetailsDto.
	 * @throws AccountNotFoundException if the account with the given ID is not found.
	 */
	@GetMapping("/{accountId}/accounts-details")
	public ResponseEntity<AccountDetailsDto> getAccountDetailsById(@PathVariable String accountId)
			throws AccountNotFoundException {
		log.info("Calling getAccountDetailsById - AccountRestAPI");
		var accountDetailsDto = accountService.getAccountDetailsById(accountId);
		return ResponseEntity.ok().body(accountDetailsDto);
	}
	
	/**
     * Create a current account for a given customer.
     *
     * @param customerId         The ID of the customer for whom the account is created.
     * @param currentAccountDto  The DTO containing details for creating the current account.
     * @return ResponseEntity containing the created CurrentAccountDto.
     * @throws CustomerNotFoundException if the customer with the specified ID is not found.
     */
	@PostMapping("/current/{customerId}")
	public ResponseEntity<CurrentAccountDto> createCurrentAccount(
			@PathVariable Long customerId,
			@Valid @RequestBody CurrentAccountDto currentAccountDto
			) throws CustomerNotFoundException {
		log.info("Call createCurrentAccount - AccountRestAPI");
		var createdCurrentAccountDto = accountService.createAccount(customerId, currentAccountDto,
				CurrentAccountDto.class);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdCurrentAccountDto);
	}
	
	/**
	 * Create a savings account for a customer.
	 *
	 * @param customerId         The ID of the customer.
	 * @param savingsAccountDto  Details of the savings account to create.
	 * @return ResponseEntity containing the created SavingsAccountDto.
	 * @throws CustomerNotFoundException if the customer with the given ID is not found.
	 */
	@PostMapping("/savings/{customerId}")
	public ResponseEntity<SavingsAccountDto> createSavingsAccount(
			@PathVariable Long customerId,
			@Valid @RequestBody SavingsAccountDto savingsAccountDto
			) throws CustomerNotFoundException {
		log.info("Invoking createSavingsAccount - AccountRestAPI");
		var createdSavingsAccountDto = accountService.createAccount(customerId, savingsAccountDto,
				SavingsAccountDto.class);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdSavingsAccountDto);
	}
	
	/**
	 * Update an existing current account.
	 *
	 * @param accountId         The ID of the current account to update.
	 * @param currentAccountDto The DTO containing updated details for the current account.
	 * @return ResponseEntity containing the updated CurrentAccountDto.
	 * @throws AccountNotFoundException if the current account with the specified ID is not found.
	 */
	@PutMapping("/current/{accountId}")
	public ResponseEntity<CurrentAccountDto> updateCurrentAccount(
			@PathVariable String accountId, 
			@Valid @RequestBody CurrentAccountDto currentAccountDto
			) throws AccountNotFoundException {
		log.info("Call updateCurrentAccount - AccountRestAPI");
		currentAccountDto.setId(accountId);
		var updatedCurrentAccountDto = (CurrentAccountDto) accountService.updateAccount(accountId, currentAccountDto);
		return ResponseEntity.ok().body(updatedCurrentAccountDto);
	}
	
	/**
     * Update an existing savings account.
     *
     * @param accountId         The ID of the savings account to update.
     * @param currentAccountDto The DTO containing updated details for the savings account.
     * @return ResponseEntity containing the updated SavingsAccountDto.
     * @throws AccountNotFoundException if the savings account with the specified ID is not found.
     */
	@PutMapping("/savings/{accountId}")
	public ResponseEntity<SavingsAccountDto> updateSavingsAccount(
			@PathVariable String accountId, 
			@Valid @RequestBody SavingsAccountDto savingsAccountDto
			) throws AccountNotFoundException {
		log.info("Call updateSavingsAccount - AccountRestAPI");
		savingsAccountDto.setId(accountId);
		var updatedSavingsAccountDto = (SavingsAccountDto) accountService.updateAccount(accountId, savingsAccountDto);
		return ResponseEntity.ok().body(updatedSavingsAccountDto);
	}
	
	/**
	 * Delete an account by ID.
	 *
	 * @param accountId The ID of the account to delete.
	 * @return ResponseEntity with a success message.
	 * @throws AccountNotFoundException if the account with the given ID is not found.
	 */
	@DeleteMapping("/{accountId}")
	public ResponseEntity<String> deleteAccount(@PathVariable String accountId) throws AccountNotFoundException {
		log.info("Invoking deleteAccount - AccountRestAPI");
		accountService.deleteAccount(accountId);
		return new ResponseEntity<>(MessageUtil.ACCOUNT_SUCCESS_DELETE, HttpStatus.OK);
	}
	
	/**
	 * Get operations by account ID.
	 *
	 * @param accountId The account ID.
	 * @return ResponseEntity containing the operations or an error message.
	 * @throws AccountNotFoundException If the account with the given ID is not found.
	 * @throws NoResultException        If no operations are found.
	 */
	@GetMapping("/{accountId}/operations")
	public ResponseEntity<List<OperationDto>> getAccountOperations(@PathVariable String accountId) 
		throws AccountNotFoundException, NoResultException {
		var operationDtos = accountService.getOperationsByAccountId(accountId);
		return ResponseEntity.ok().body(operationDtos);
	}
	
	/**
     * Retrieve the account history for a specific account using pagination.
     *
     * @param accountId The unique identifier of the account.
     * @param page       The page number for pagination.
     * @param size       The size of each page for pagination.
     * @return ResponseEntity containing the account history with paginated operations.
     * @throws Exception If an error occurs during account history retrieval.
     */
	@GetMapping("/{accountId}/history")
	public ResponseEntity<AccountHistoryDto> getAccountHistoryByPage(
			@PathVariable String accountId,
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "5") int size) throws Exception {
			var accountHistory = accountService.accountHistoryByPage(accountId, page, size);
			return ResponseEntity.ok(accountHistory);
	}

}
