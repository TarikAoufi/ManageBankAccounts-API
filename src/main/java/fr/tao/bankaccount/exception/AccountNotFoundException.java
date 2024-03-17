package fr.tao.bankaccount.exception;

import fr.tao.bankaccount.util.MessageUtil;

/**
 * Exception thrown when an account is not found.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
public class AccountNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1293212731624557459L;
	
	/**
     * Constructs an AccountNotFoundException with the specified account ID.
     * 
     * @param accountId The ID of the account that was not found.
     */
	public AccountNotFoundException(String accountId) {
        super(MessageUtil.ACCOUNT_NOT_FOUND + accountId);
    }
}