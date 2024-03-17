package fr.tao.bankaccount.exception;

/**
 * Exception thrown when an operation fails for some reason.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
public class OperationFailedException extends Exception {

	private static final long serialVersionUID = 2920372907925287699L;

	/**
	 * Constructs a new OperationFailedException with the specified detail message.
	 *
	 * @param message The detail message explaining the reason for the exception.
	 */
	public OperationFailedException(String message) {
		super(message);
	}

	/**
	 * Constructs a new OperationFailedException with the specified detail message
	 * and cause.
	 *
	 * @param message The detail message explaining the reason for the exception.
	 * @param cause   The cause of the exception.
	 */
	public OperationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
