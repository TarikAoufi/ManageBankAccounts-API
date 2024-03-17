package fr.tao.bankaccount.service;

import java.util.List;

import fr.tao.bankaccount.dto.account.AccountDetailsDto;
import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.account.AccountHistoryDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.exception.AccountNotFoundException;
import fr.tao.bankaccount.exception.CustomerNotFoundException;
import jakarta.persistence.NoResultException;

/**
 * An interface representing the service for managing user accounts.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
public interface AccountService {
	
	/**
     * Retrieves a list of all accounts.
     *
     * @return A list of {@link AccountDto} objects representing all accounts.
     * @throws NoResultException If no accounts are found.
     */
	public List<AccountDto> getAllAccount() throws NoResultException;
	
	/**
     * Retrieves an account by its ID.
     *
     * @param accountId The ID of the account to retrieve.
     * @return The {@link AccountDto} object representing the account.
     * @throws AccountNotFoundException If the account with the specified ID is not found.
     */
	public AccountDto getAccountById(String accountId) throws AccountNotFoundException;
	
	/**
     * Retrieves a list of all account details.
     *
     * @return A list of {@link AccountDetailsDto} objects representing all account details.
     * @throws NoResultException If no account details are found.
     */
	public List<AccountDetailsDto> getAllAccountDetails() throws NoResultException;
	
	/**
     * Retrieves account details by its ID.
     *
     * @param accountId The ID of the account details to retrieve.
     * @return The {@link AccountDetailsDto} object representing the account details.
     * @throws AccountNotFoundException If the account details with the specified ID is not found.
     */
	public AccountDetailsDto getAccountDetailsById(String accountId) throws AccountNotFoundException;

	/**
     * Creates a new account of the specified type for a given customer.
     *
     * @param customerId The ID of the customer for whom the account is created.
     * @param accountDto The {@link AccountDto} object representing the account to be created.
     * @param type       The class type of the account to be created.
     * @param <T>        A type parameter extending {@link AccountDto}.
     * @return The created account of the specified type.
     * @throws CustomerNotFoundException If the customer with the specified ID is not found.
     */
	public <T extends AccountDto> T createAccount(Long customerId, AccountDto accountDto, Class<T> type) throws CustomerNotFoundException;
	
	
	/**
     * Updates an existing account with the provided account information.
     *
     * @param accountId  The ID of the account to be updated.
     * @param accountDto The {@link AccountDto} object representing the updated account information.
     * @return The updated {@link AccountDto} object.
     * @throws AccountNotFoundException If the account with the specified ID is not found.
     */
	public AccountDto updateAccount(String accountId, AccountDto accountDto) throws AccountNotFoundException;
	
	/**
     * Deletes an account by its ID.
     *
     * @param accountId The ID of the account to be deleted.
     * @throws AccountNotFoundException If the account with the specified ID is not found.
     */
	public void deleteAccount(String accountId) throws AccountNotFoundException;
	
	/**
     * Retrieves a list of operations associated with a specific account.
     *
     * @param accountId The ID of the account for which operations are retrieved.
     * @return A list of {@link OperationDto} objects representing account operations.
     * @throws AccountNotFoundException If the account with the specified ID is not found.
     * @throws NoResultException        If no operations are found for the account.
     */
	public List<OperationDto> getOperationsByAccountId(String accountId) throws AccountNotFoundException, NoResultException;
	
	/**
     * Retrieves account history by page.
     *
     * @param accountId The ID of the account for which the history is retrieved.
     * @param page      The page number.
     * @param size      The number of items per page.
     * @return The {@link AccountHistoryDto} object representing the account history for the specified page.
     * @throws Exception If an error occurs during the retrieval of account history.
     */
	public AccountHistoryDto accountHistoryByPage(String accountId, int page, int size) throws Exception;

}
