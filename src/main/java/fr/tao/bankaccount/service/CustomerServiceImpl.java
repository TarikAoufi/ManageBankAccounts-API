package fr.tao.bankaccount.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.customer.CustomerDto;
import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.exception.CustomerNotFoundException;
import fr.tao.bankaccount.mapper.AccountMapper;
import fr.tao.bankaccount.mapper.CustomerMapper;
import fr.tao.bankaccount.mapper.OperationMapper;
import fr.tao.bankaccount.repository.CustomerRepository;
import fr.tao.bankaccount.service.validation.ValidationService;
import fr.tao.bankaccount.util.MessageUtil;
import jakarta.persistence.NoResultException;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the {@link CustomerService} interface providing
 * customer-related operations. Utilizes various mappers and services for data
 * transformation and validation.
 *
 *
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
	
	/**
     * Repository for accessing and managing customer data.
     */
	@NonNull
    private CustomerRepository customerRepository;
	
	/**
     * Mapper for transforming account-related data.
     */
	@NonNull
    private AccountMapper accountMapper;
	
	 /**
     * Mapper for transforming customer-related data.
     */
	@NonNull
    private CustomerMapper customerMapper;
	
	/**
     * Mapper for transforming operation-related data.
     */
	@NonNull
    private OperationMapper operationMapper;
	
	/**
     * Service for validating data related to customers.
     */
	@NonNull
    private ValidationService validationService;

    /**
     * {@inheritDoc}
     */
	@Override
	public List<CustomerDto> getAllCustomer() throws NoResultException {	
		log.info(" #### Getting all customers #### ");
		var customers = customerRepository.findAll();
		if (customers.isEmpty()) {
			log.warn(MessageUtil.CUSTOMERS_NO_RESULT);
			throw new NoResultException(MessageUtil.CUSTOMERS_NO_RESULT);
		}
		log.info("Retrieved {} customers", customers.size());
        return customerMapper.toCustomerDTOList(customers);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public CustomerDto getCustomerById(Long customerId) throws CustomerNotFoundException {
		log.info(" #### Getting customer by ID: {} #### ", customerId);
		var customer = customerRepository.findById(customerId)
				.orElseThrow(() -> {
					return new CustomerNotFoundException(customerId);
				});
		log.info("Retrieved customer : {}", customer);
		return customerMapper.toDto(customer);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public List<CustomerDto> getCustomersByNameContains(String name) throws NoResultException  {
		log.info(" #### Getting customers by name contains: {} #### ", name);
		var customers = customerRepository.findByNameContainsIgnoreCase(name);
		if (customers.isEmpty()) {
			log.warn(MessageUtil.CUSTOMERS_BY_NAME_NO_RESULT + name);
			throw new NoResultException(MessageUtil.CUSTOMERS_BY_NAME_NO_RESULT + name);
		}
		log.info("Retrieved {} customers with name containing: {}", customers.size(), name);
		return customerMapper.toCustomerDTOList(customers);	
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public List<AccountDto> getAllAccountsByCustomerId(Long customerId) {
		log.info(" #### Retrieve all customer accounts by customer ID: {} #### ", customerId);		
        var accounts = customerRepository.findAllAccountByCustomerId(customerId);
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
	@Transactional
	@Override
	public CustomerDto saveCustomer(CustomerDto customerDto) throws Exception {
		log.info(" #### Saving new customer: {} #### ", customerDto);

		var customer = customerMapper.toEntity(customerDto);

		// Validate the customer entity using the generic validation service
        validationService.validateAndThrow(customer);
		
		customer = customerRepository.save(customer);
		log.info("Saved customer: {}", customer);
		
		return customerMapper.toDto(customer);
	}
	
	/**
     * {@inheritDoc}
     */
	@Transactional
	@Override
	public CustomerDto updateCustomer(Long customerId, CustomerDto customerDto) throws CustomerNotFoundException, ConstraintViolationException {
		log.info(" #### Updating customer with ID: {} - New data: {} #### ", customerId, customerDto);
		
		var customer = customerRepository.findById(customerId)
				.orElseThrow(() -> {
                    log.warn(MessageUtil.CUSTOMER_NOT_FOUND + customerId);
                    return new CustomerNotFoundException(customerId);
                });        
		
        customerMapper.updateCustomerFromDTO(customerDto, customer);
        
		// Validate the customer entity using the generic validation service
		validationService.validateAndThrow(customer);

		customer = customerRepository.save(customer);
		log.info("Updated customer: {}", customer);
        return customerMapper.toDto(customer);
	}
	
	/**
     * {@inheritDoc}
     */
	@Transactional
	@Override
	public void deleteCustomer(Long customerId) throws CustomerNotFoundException  {
		log.info(" #### Deleting customer with ID: {} #### ", customerId);
        var customer = customerRepository.findById(customerId)
				.orElseThrow(() -> {
                    log.warn(MessageUtil.CUSTOMER_NOT_FOUND + customerId);
                    return new CustomerNotFoundException(customerId);
                });
        customerRepository.delete(customer);	
		log.info("Deleted customer: {}", customer);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public List<OperationDto> getOperationsByCustomerId(Long customerId) {		
		var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        var operations = customerRepository.findOperationsByCustomerId(customer.getId());
        if (operations.isEmpty()) {
        	log.warn(MessageUtil.OPERATIONS_NO_RESULT);
			throw new NoResultException(MessageUtil.OPERATIONS_NO_RESULT);
        }
        return operationMapper.mapOperations(operations);
	}
	
}
