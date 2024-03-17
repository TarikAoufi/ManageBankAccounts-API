package fr.tao.bankaccount.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.tao.bankaccount.dto.account.AccountDetailsDto;
import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.account.AccountHistoryDto;
import fr.tao.bankaccount.dto.account.CurrentAccountDto;
import fr.tao.bankaccount.dto.account.SavingsAccountDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.entity.Account;
import fr.tao.bankaccount.entity.CurrentAccount;
import fr.tao.bankaccount.entity.SavingsAccount;
import fr.tao.bankaccount.exception.AccountNotFoundException;
import fr.tao.bankaccount.exception.CustomerNotFoundException;
import fr.tao.bankaccount.exception.record.InvalidatedParams;
import fr.tao.bankaccount.mapper.AccountMapper;
import fr.tao.bankaccount.mapper.CustomerMapper;
import fr.tao.bankaccount.mapper.OperationMapper;
import fr.tao.bankaccount.repository.AccountRepository;
import fr.tao.bankaccount.repository.CustomerRepository;
import fr.tao.bankaccount.util.MessageUtil;
import fr.tao.bankaccount.validation.UUIDValidator;
import fr.tao.bankaccount.validation.UUIDValidator.InvalidUUIDFormatException;
import jakarta.persistence.NoResultException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@inheritDoc}
 * Service implementation for managing accounts.
 * This class provides CRUD operations and additional functionalities related to accounts.
 * Uses {@link AccountRepository} for data access, {@link AccountMapper} for mapping entities to DTOs,
 * and {@link CustomerRepository} for customer-related operations.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Service 
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
	
	/**
     * The repository for accessing account data.
     */
	@NonNull private final AccountRepository accountRepository;
	
	/**
     * The repository for accessing customer data.
     */
	@NonNull private final CustomerRepository customerRepository;
	
	/**
     * The mapper for converting between Account entities and DTOs.
     */
	@NonNull private final AccountMapper accountMapper;
	
	/**
     * The mapper for converting between Operation entities and DTOs.
     */
	@NonNull private final OperationMapper operationMapper;
	
	/**
     * The mapper for converting between Customer entities and DTOs.
     */
	@NonNull private final CustomerMapper customerMapper;
	
	/**
     * {@inheritDoc}
     */
	public Account findAccountById(String accountId) throws AccountNotFoundException {
		return accountRepository.findById(accountId)
				.orElseThrow(() -> {
					log.error(MessageUtil.ACCOUNT_NOT_FOUND + accountId);
					return new AccountNotFoundException(accountId);
				});
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public List<AccountDto> getAllAccount() throws NoResultException {
		log.info(" #### Get all accounts #### ");
		var accounts = accountRepository.findAll();
		if (accounts.isEmpty()) {
			log.warn(MessageUtil.ACCOUNTS_NO_RESULT);
			throw new NoResultException(MessageUtil.ACCOUNTS_NO_RESULT);
		}
		log.info("Retrieved {} accounts.", accounts.size());
		return accounts.stream().map(accountMapper::mapAccount).toList();
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public AccountDto getAccountById(String accountId) throws AccountNotFoundException {
		log.info(" #### Getting account by ID: {} #### ", accountId);
		var account = findAccountById(accountId);
		log.info("Retrieved account : {}", account);
		return accountMapper.mapAccount(account);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public List<AccountDetailsDto> getAllAccountDetails() throws NoResultException {
		log.info(" #### Get all accounts with details #### ");
		var accounts = accountRepository.findAllWithCustomerAndOperations();
		if (accounts.isEmpty()) {
			log.warn(MessageUtil.CUSTOMERS_NO_RESULT);
			throw new NoResultException(MessageUtil.CUSTOMERS_NO_RESULT);
		}
		log.info("Retrieved {} accounts with details", accounts.size());
		return accounts.stream().map(accountMapper::mapAccountDetails).toList();
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public AccountDetailsDto getAccountDetailsById(String accountId) throws AccountNotFoundException {
		log.info(" #### Get account with details by ID: {} #### ", accountId);
		var account = findAccountById(accountId);	
		log.info("Retrieved account with details : {}", account);
		return accountMapper.mapAccountDetails(account);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	@Transactional
	public <T extends AccountDto> T createAccount(Long customerId, AccountDto accountDto, Class<T> type) throws CustomerNotFoundException {
		log.info(" #### Creating account for customer with ID: {} - account data: {} #### ", customerId, accountDto);
	    var customer = customerRepository.findById(customerId)
	        .orElseThrow(() -> new CustomerNotFoundException(customerId));

	    accountDto.setCustomerDto(customerMapper.toDto(customer));
	    var account = accountMapper.mapAccountDto(accountDto);
	    var createdAccount = accountRepository.save(account);

	    log.info(" ####### {} Account is created with ID: {} , for customer ID: {}", 
        		createdAccount.getAccountType(),
        		createdAccount.getId(), customerId);

	    return type.cast(accountMapper.mapAccount(createdAccount));
	}
	
	/**
     * {@inheritDoc}
     */
	@Transactional
	@Override
	public AccountDto updateAccount(String accountId, AccountDto accountDto) throws AccountNotFoundException {
		log.info(" #### Updating account with ID: {} - New data: {} #### ", accountId, accountDto);
		var account = findAccountById(accountId);
		
		switch (account.getAccountType()) {
		case CURRENT:
			accountMapper.updateCurrentAccountFromDto((CurrentAccountDto) accountDto, (CurrentAccount) account);
			break;
		case SAVINGS:
			accountMapper.updateSavingsAccountFromDto((SavingsAccountDto) accountDto, (SavingsAccount) account);
			break;
		default:
			accountMapper.updateAccountFromDto(accountDto, account);
		}
		
	    var createdAccount = accountRepository.save(account);
	    return accountMapper.mapAccount(createdAccount);
	}
	
	/**
     * {@inheritDoc}
     */
	@Transactional
	@Override
	public void deleteAccount(String accountId) throws AccountNotFoundException {
		log.info(" #### Delete account with ID: {} #### ", accountId);
		var account = findAccountById(accountId);       
        accountRepository.delete(account);		
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public List<OperationDto> getOperationsByAccountId(String accountId) {
		var account = findAccountById(accountId);
		var operations = account.getOperations()
				.stream()
				.map(operationMapper::toDto)
				.toList();
		if (operations.isEmpty()) {
        	log.warn(MessageUtil.OPERATIONS_NO_RESULT);
			throw new NoResultException(MessageUtil.OPERATIONS_NO_RESULT);
        }
		return operations;
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public AccountHistoryDto accountHistoryByPage(String accountId, int page, int size) {
		log.debug("Received request for account history with ID: {}", accountId);
		try {
			// Validate the UUID format
            UUID validatedUUID = UUIDValidator.validateAndConvertToUUID(accountId);
            
			var account = findAccountById(validatedUUID.toString());
			var operations = accountRepository.findOperationsByAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
	
			var operationDtos = operations.getContent()
					.stream()
					.map(operationMapper::toDto)
					.toList();
			
			return AccountHistoryDto.builder()
					.accountDto(accountMapper.mapAccount(account))
					.currentPage(page)
					.totalPage(operations.getTotalPages())
					.pageSize(size)
					.operationDtos(operationDtos)
					.build();
		} catch (InvalidUUIDFormatException e) {
            log.error("Invalid account ID format provided: {}", accountId);
			throw new InvalidUUIDFormatException("Invalid account ID format provided",
					Arrays.asList(InvalidatedParams.builder()
							.cause("Account ID should be a valid UUID format")
							.attribute("accountId")
							.build())
					);      
        } catch (Exception e) {
            log.error("Error processing request for account history with ID: {}", accountId);
            throw e;
        }
	}
	

}
