package fr.tao.bankaccount.service;

import java.util.List;

import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.customer.CustomerDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.exception.CustomerNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.validation.ConstraintViolationException;

/**
 * This interface defines a set of methods for managing customer data.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
public interface CustomerService {
	
	/**
     * Retrieves a list of all customers.
     *
     * @return A list of CustomerDto objects representing all customers.
     * @throws NoResultException if no matching customers are found.
     */
	public List<CustomerDto> getAllCustomer() throws NoResultException;
	
	/**
     * Retrieves customer information by the specified customer ID.
     *
     * @param customerId The unique identifier of the customer.
     * @return A CustomerDto object representing the customer with the given ID.
     * @throws CustomerNotFoundException If the customer with the specified ID is not found.
     */
	public CustomerDto getCustomerById(Long customerId) throws CustomerNotFoundException;
	
	/**
     * Retrieves a list of customers whose names contain the specified keyword.
     *
     * @param name The keyword to search for in customer names.
     * @return A list of CustomerDto objects representing customers with names containing the keyword.
     * @throws NoResultException if no matching customers are found.
     */
	public List<CustomerDto> getCustomersByNameContains(String name) throws NoResultException;
		
	/**
     * Saves a new customer or updates an existing customer.
     *
     * @param customerDto The CustomerDto object containing the customer information.
     * @return A CustomerDto object representing the saved customer.
     * @throws Exception If an error occurs during the save operation.
     */
	public CustomerDto saveCustomer(CustomerDto customerDto) throws Exception;		
	
	/**
     * Updates the information of an existing customer.
     *
     * @param customerId  The unique identifier of the customer to be updated.
     * @param customerDto The CustomerDto object containing the updated customer information.
     * @return A CustomerDto object representing the updated customer.
     * @throws CustomerNotFoundException   If the customer with the given ID is not found.
	 * @throws ConstraintViolationException If there are validation constraints on the updated data.
     */
	public CustomerDto updateCustomer(Long customerId, CustomerDto customerDto) throws CustomerNotFoundException, ConstraintViolationException;
	
	/**
     * Deletes a customer by the specified customer ID.
     *
     * @param customerId The unique identifier of the customer to be deleted.
     * @throws CustomerNotFoundException If the customer with the given ID is not found.
     */
	public void deleteCustomer(Long customerId) throws CustomerNotFoundException;
	
	/**
	 * Retrieve all accounts for a specific customer.
	 *
	 * @param customerId The ID of the customer.
	 * @return List of AccountDto representing all accounts associated with the customer.
	 * @throws CustomerNotFoundException If the customer with the given ID is not found.
	 * @throws NoResultException         If no accounts are found for the specified customer.
	 */
	public List<AccountDto> getAllAccountsByCustomerId(Long customerId) throws CustomerNotFoundException, NoResultException;
	
	/**
	 * Retrieve all operations for a specific customer.
	 *
	 * @param customerId The ID of the customer.
	 * @return List of OperationDto representing all operations associated with the customer.
	 * @throws CustomerNotFoundException If the customer with the given ID is not found.
	 * @throws NoResultException         If no operations are found for the specified customer.
	 */
	public List<OperationDto> getOperationsByCustomerId(Long customerId) throws CustomerNotFoundException, NoResultException;

}
