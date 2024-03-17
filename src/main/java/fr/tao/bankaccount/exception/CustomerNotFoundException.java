package fr.tao.bankaccount.exception;

import fr.tao.bankaccount.util.MessageUtil;

/**
 * Exception thrown when a customer with the specified ID is not found.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
public class CustomerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1184429604946529495L;
	
	/**
     * Constructs a new CustomerNotFoundException with the specified customer ID.
     *
     * @param id The ID of the customer that was not found.
     */
	public CustomerNotFoundException(Long id) {
        super(MessageUtil.CUSTOMER_NOT_FOUND + id);
    }
}