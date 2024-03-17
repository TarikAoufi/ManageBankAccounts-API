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

import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.customer.CustomerDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.exception.CustomerNotFoundException;
import fr.tao.bankaccount.service.CustomerService;
import fr.tao.bankaccount.util.MessageUtil;
import jakarta.persistence.NoResultException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class represents a RESTful controller for managing customer data.
 * 
 * It handles various operations related to customers, such as retrieving,
 * creating, updating, and deleting customers.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@RestController
@CrossOrigin("*")
@RequestMapping(produces = "application/json", value = "/api/bank/customers")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CustomerRestAPI {

	@NonNull
	private final CustomerService customerService;

	/**
	 * Get a list of all customers.
	 *
	 * @return ResponseEntity<List<CustomerDto>: A response entity containing a list
	 *         of customer data in JSON format.
	 * @throws NoResultException if no matching customers are found.
	 */
	@GetMapping
	public ResponseEntity<List<CustomerDto>> getCustomers() throws NoResultException {
		log.info("Invoking getCustomers - CustomerRestAPI");
		var customers = customerService.getAllCustomer();
		return ResponseEntity.ok(customers);
	}

	/**
	 * Get a customer by their ID.
	 *
	 * @param id The ID of the customer to retrieve.
	 * @return ResponseEntity<CustomerDto>: A response entity containing customer
	 *         data in JSON format.
	 * @throws CustomerNotFoundException if the specified customer is not found.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) throws CustomerNotFoundException {
		log.info("Calling getCustomerById - CustomerRestAPI");
		var customer = customerService.getCustomerById(id);
		return ResponseEntity.ok(customer);
	}

	/**
	 * Search for customers by name.
	 *
	 * @param name The name or part of the name to search for.
	 * @return ResponseEntity<List<CustomerDto>: A response entity containing a list
	 *         of matching customer data in JSON format.
	 * @throws NoResultException if no matching customers are found.
	 */
	@GetMapping("/search")
	public ResponseEntity<List<CustomerDto>> searchCustomersByName(@RequestParam String name) throws NoResultException {
		log.info("searchCustomersByName - REST request: Getting customers by name contains: {}", name);
		var customerDtos = customerService.getCustomersByNameContains(name);
		return ResponseEntity.ok().body(customerDtos);
	}

	/**
	 * Create a new customer.
	 *
	 * @param customerDto The customer data to be created.
	 * @return ResponseEntity<CustomerDto>: A response entity containing the newly
	 *         created customer data in JSON format.
	 * @throws Exception if there is an error while processing the request.
	 */
	@PostMapping
	public ResponseEntity<CustomerDto> saveCustomer(@Valid @RequestBody CustomerDto customerDto) throws Exception {
		log.info("saveCustomer - REST request: Saving new customer: {}", customerDto);
		var createdCustomer = customerService.saveCustomer(customerDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
	}

	/**
	 * Update customer details by ID.
	 *
	 * @param customerId  The ID of the customer to update.
	 * @param customerDto New details for the customer.
	 * @return ResponseEntity containing the updated CustomerDto.
	 * @throws CustomerNotFoundException    If the customer with the given ID is not
	 *                                      found.
	 * @throws ConstraintViolationException If there are validation constraints on
	 *                                      the updated data.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<CustomerDto> updateCustomer(@PathVariable("id") Long customerId,
			@Valid @RequestBody CustomerDto customerDto)
			throws CustomerNotFoundException, ConstraintViolationException {
		log.info("updateCustomer - REST request: Updating customer with ID: {} - New data: {}", customerId,
				customerDto);
		var updatedCustomer = customerService.updateCustomer(customerId, customerDto);
		return ResponseEntity.ok().body(updatedCustomer);
	}

	/**
	 * Delete a customer by their ID.
	 *
	 * @param customerId The ID of the customer to delete.
	 * @return ResponseEntity<String>: A response entity with a message indicating
	 *         the success of the deletion.
	 * @throws CustomerNotFoundException If the customer with the given ID is not
	 *                                   found.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCustomer(@PathVariable("id") Long customerId) throws CustomerNotFoundException {
		log.info("deleteCustomer - REST request: Deleting customer with ID: {}", customerId);
		customerService.deleteCustomer(customerId);
		return ResponseEntity.ok().body(MessageUtil.CUSTOMER_SUCCESS_DELETE);
	}

	/**
	 * Retrieve all accounts for a specific customer.
	 *
	 * @param customerId The ID of the customer.
	 * @return ResponseEntity containing a list of AccountDto representing all
	 *         accounts associated with the customer.
	 * @throws CustomerNotFoundException If the customer with the given ID is not
	 *                                   found.
	 * @throws NoResultException         If no accounts are found for the specified
	 *                                   customer.
	 */
	@GetMapping("/{customerId}/accounts")
	public ResponseEntity<List<AccountDto>> getAllAccountsByCustomerId(@PathVariable Long customerId)
			throws CustomerNotFoundException, NoResultException {
		var customerIdRequired = customerService.getCustomerById(customerId);
		var accountDtos = customerService.getAllAccountsByCustomerId(customerIdRequired.getId());
		return ResponseEntity.ok().body(accountDtos);
	}

	/**
	 * Retrieve all operations for a specific customer.
	 *
	 * @param customerId The ID of the customer.
	 * @return ResponseEntity containing a list of OperationDto representing all
	 *         operations associated with the customer.
	 * @throws CustomerNotFoundException If the customer with the given ID is not
	 *                                   found.
	 * @throws NoResultException         If no operations are found for the
	 *                                   specified customer.
	 */
	@GetMapping("/{customerId}/operations")
	public ResponseEntity<List<OperationDto>> getCustomerOperations(@PathVariable Long customerId)
			throws CustomerNotFoundException, NoResultException {
		var customerIdRequired = customerService.getCustomerById(customerId);
		var operationDtos = customerService.getOperationsByCustomerId(customerIdRequired.getId());
		log.info("Fetching operations for customer with ID: {} - CustomerRestAPI", customerIdRequired.getId());
		return ResponseEntity.ok().body(operationDtos);
	}

}
