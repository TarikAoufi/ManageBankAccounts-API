package fr.tao.bankaccount.exception;

import fr.tao.bankaccount.util.MessageUtil;

/**
 * Exception thrown when an operation with the specified ID is not found.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
public class OperationNotFoundException extends Exception {

	private static final long serialVersionUID = 3512547443072485816L;

	/**
	 * Constructs a new OperationNotFoundException with the specified operation ID.
	 *
	 * @param operationId The ID of the operation that was not found.
	 */
	public OperationNotFoundException(Long operationId) {
		super(MessageUtil.OPERATION_NOT_FOUND + operationId);
	}
}