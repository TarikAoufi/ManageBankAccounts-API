package fr.tao.bankaccount.exception;

/**
 * Exception thrown when an operation cannot be performed due to insufficient
 * balance in an account.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
public class InsufficientBalanceException extends Exception {

	private static final long serialVersionUID = -7109860617137093247L;
	
	/**
	 * Constructs a new InsufficientBalanceException with the specified detail
	 * message.
	 *
	 * @param message The detail message explaining the reason for the exception.
	 */
	public InsufficientBalanceException(String message) {
		super(message);
	}

}
