package fr.tao.bankaccount.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.dto.operation.OperationRequestDto;
import fr.tao.bankaccount.dto.operation.TransferDto;
import fr.tao.bankaccount.enums.OperationType;
import fr.tao.bankaccount.exception.InsufficientBalanceException;
import fr.tao.bankaccount.exception.OperationNotFoundException;
import fr.tao.bankaccount.mapper.AccountMapper;
import fr.tao.bankaccount.mapper.OperationMapper;
import fr.tao.bankaccount.repository.OperationRepository;
import fr.tao.bankaccount.service.validation.ValidationService;
import fr.tao.bankaccount.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for performing banking operations such as deposit,
 * withdrawal, and transfer.
 *
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OperationServiceImpl implements OperationService {
	
	/**
     * Service for managing accounts.
     */
	@NonNull
	private final AccountService accountService;
	
	/**
     * The repository for managing banking operations.
     */
	@NonNull
	private final OperationRepository operationRepository;
	
	/**
     * Mapper for converting between operation DTOs and entities.
     */
	@NonNull
	private final OperationMapper operationMapper;
	
	/**
     * Mapper for converting between account DTOs and entities.
     */
	@NonNull
	private final AccountMapper accountMapper;
	
	/**
     * Service for validating entities against constraints.
     */
	@NonNull
    private final ValidationService validationService;
	
	/**
     * {@inheritDoc}
     */
	@Transactional
	public Object performOperation(OperationRequestDto request) throws Exception {
		String operationDescription = "";
		switch (request.getOperationType()) {
		case DEPOSIT:
			operationDescription = createOperationDescription(OperationType.DEPOSIT, request.getAmount(), null, null, true);
			return deposit(request.getAccountId(), request.getAmount(), operationDescription);
		case WITHDRAWAL:
			operationDescription = createOperationDescription(OperationType.WITHDRAWAL, request.getAmount(), null, null,
					true);
			return withdraw(request.getAccountId(), request.getAmount(), operationDescription);
		case TRANSFER:
			if (request.getSourceAccountId().equals(request.getTargetAccountId())) {
                throw new IllegalArgumentException(MessageUtil.SAME_SOURCE_AND_TARGET_ACCOUNT_ERROR);
            }
			return transfer(request.getSourceAccountId(), request.getTargetAccountId(), request.getAmount());
		default:
			throw new UnsupportedOperationException(MessageUtil.UNSUPPORTED_OPERATION_ERROR);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Transactional
	@Override
	public void deleteOperation(Long operationId) throws OperationNotFoundException {
		log.info(" #### Deleting operation with ID: {} #### ", operationId);
        var operation = operationRepository.findById(operationId)
				.orElseThrow(() -> {
                    log.warn(MessageUtil.OPERATION_NOT_FOUND + operationId);
                    return new OperationNotFoundException(operationId);
                });
        operationRepository.delete(operation);	
		log.info("Deleted operation: {}", operation);
	}
	
	/**
	 * Handles the deposit operation for a given account.
	 *
	 * @param accountId   The ID of the target account.
	 * @param amount      The amount to be deposited.
	 * @param description The description for the deposit operation.
	 * @return The created OperationDto for the deposit.
	 * @throws Exception If an error occurs during the deposit operation.
	 * @see OperationType#DEPOSIT
	 */
	protected OperationDto deposit(String accountId, BigDecimal amount, String description) throws Exception {
		try {
			// Logic for deposit
			var accountDto = accountService.getAccountById(accountId);
			accountDto.setBalance(accountDto.getBalance().add(amount));
			accountDto = accountService.updateAccount(accountId, accountDto);
			return createOperation(accountDto, OperationType.DEPOSIT, amount, description);
		} catch (Exception e) {
	        log.error("Error during deposit operation");
	        throw e;
	    }
	}
	
	/**
	 * Handles the withdrawal operation for a given account.
	 *
	 * @param accountId   The ID of the target account.
	 * @param amount      The amount to be withdrawn.
	 * @param description The description for the withdrawal operation.
	 * @return The created OperationDto for the withdrawal.
	 * @throws Exception If an error occurs during the withdrawal operation.
	 * @throws InsufficientBalanceException If the account balance is insufficient for withdrawal.
	 * @see OperationType#WITHDRAWAL
	 */
	protected OperationDto withdraw(String accountId, BigDecimal amount, String description) throws Exception {
		try {
			// Logic for withdrawal
			var accountDto = accountService.getAccountById(accountId);
			if (accountDto.getBalance().compareTo(amount) < 0) {
				var details = "Balance=" + accountDto.getBalance() + " < Amount=" + amount;
				throw new InsufficientBalanceException("Insufficient balance to withdraw! " + details);
			}
			accountDto.setBalance(accountDto.getBalance().subtract(amount));
			accountDto = accountService.updateAccount(accountId, accountDto);
			return createOperation(accountDto, OperationType.WITHDRAWAL, amount, description);
		} catch (Exception e) {
	        log.error("Error during withdraw operation");
			throw e;
	    }
	}
	
	/**
	 * Handles the transfer operation between two accounts.
	 *
	 * @param sourceAccountId The ID of the source account.
	 * @param targetAccountId The ID of the target account.
	 * @param amount          The amount to be transferred.
	 * @return The TransferDto containing source and target account operation details.
	 * @throws Exception If an error occurs during the transfer operation.
	 * @see OperationType#TRANSFER
	 */
	protected TransferDto transfer(String sourceAccountId, String targetAccountId, BigDecimal amount) throws Exception {
		try {
			// Logic for transfer
			var description = createOperationDescription(OperationType.TRANSFER, amount, sourceAccountId, targetAccountId, true);
			var sourceAccountOperation = withdraw(sourceAccountId, amount, description);
			
			description = createOperationDescription(	OperationType.TRANSFER, amount, sourceAccountId, targetAccountId, false);
			var targetAccountOperation = deposit(targetAccountId, amount, description);

			return TransferDto.builder()
					.sourceAccountOperationDto(sourceAccountOperation)
					.targetAccountOperationDto(targetAccountOperation)
					.build();
		} catch (Exception e) {
	        log.error("Error during transfer operation");
			throw e;
	    }

	}
	
	/**
	 * Creates and saves an operation for a given account.
	 *
	 * @param accountDto    The AccountDto associated with the operation.
	 * @param operationType The type of the operation (e.g., DEPOSIT, WITHDRAWAL).
	 * @param amount        The amount involved in the operation.
	 * @param description   The description for the operation.
	 * @return The created OperationDto.
	 * @throws Exception If an error occurs during the operation creation and saving.
	 */
	protected OperationDto createOperation(AccountDto accountDto, OperationType operationType, BigDecimal amount,
			String description) throws Exception {
		try {
			// create operation
			var operationDto = OperationDto.builder()
					.accountDto(accountDto)
					.operationType(operationType)
					.amount(amount)
					.description(description)
					.build();
			
			var operation = operationMapper.toEntity(operationDto);
			
			// Additional validation with this generic service
		 	validationService.validateAndThrow(operation);
			
			operation.setAccount(accountMapper.toEntity(accountDto));
			// save operation
			var savedOperation = operationRepository.save(operation);
			return operationMapper.toDto(savedOperation);
		} catch (Exception e) {
			// Exception will be handled by the global exception handler
	        log.error("Error creating " + operationType + " operation");
	        throw e;
	    }
	}
	
	/**
	 * Creates a descriptive string for an operation based on its type and parameters.
	 *
	 * @param operationType     The type of the operation (e.g., DEPOSIT, WITHDRAWAL, TRANSFER).
	 * @param amount            The amount involved in the operation.
	 * @param sourceAccountId   The ID of the source account (for transfer).
	 * @param targetAccountId   The ID of the target account (for transfer).
	 * @param isSourceToTarget  Indicates if it's a transfer from source to target or vice versa.
	 * @return The operation description.
	 */
	private String createOperationDescription(OperationType operationType, BigDecimal amount, String sourceAccountId,
			String targetAccountId, boolean isSourceToTarget) {
		return switch (operationType) {
		case DEPOSIT -> isSourceToTarget ? "Amount Credited : " + amount : "";
		case WITHDRAWAL -> isSourceToTarget ? "Amount Debited : " + amount : "";
		case TRANSFER -> "Transfer Amount " + amount + (isSourceToTarget ? " to" : " from") + " accountId: "
				+ (isSourceToTarget ? targetAccountId.substring(0, Math.min(targetAccountId.length(), 8)) + ".."
						: sourceAccountId.substring(0, Math.min(sourceAccountId.length(), 8)) + "..");
		};
	}
	

}
