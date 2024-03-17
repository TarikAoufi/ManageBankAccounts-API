package fr.tao.bankaccount.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import fr.tao.bankaccount.entity.Account;
import fr.tao.bankaccount.entity.Customer;
import fr.tao.bankaccount.entity.Operation;


/**
 * Repository interface for managing Customer entities.
 * This interface extends JpaRepository to inherit basic CRUD operations for Customer entities.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@RepositoryRestResource
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	
	/**
     * Finds a list of customers whose names contain the specified keyword (case-insensitive).
     *
     * @param keyword The keyword to search for in customer names.
     * @return List of customers matching the search criteria.
     */
	List<Customer> findByNameContainsIgnoreCase(String keyword);
	
	 /**
     * Finds a list of accounts associated with a customer using a custom JPQL query.
     *
     * @param customerId The ID of the customer whose accounts are to be retrieved.
     * @return List of accounts associated with the specified customer.
     */
	@Query("SELECT distinct a FROM Account a LEFT JOIN FETCH a.customer c WHERE c.id = :customerId ")
	List<Account> findAllAccountByCustomerId(Long customerId);
	
	/**
     * Finds all operations associated with the accounts of a customer.
     *
     * @param customerId The ID of the customer whose operations are to be retrieved.
     * @return List of operations associated with the specified customer's accounts.
     */
	List<Operation> findAllOperationsByAccounts_Customer_Id(Long customerId);
	
	/**
     * Finds all operations associated with a customer using a custom JPQL query.
     *
     * @param customerId The ID of the customer whose operations are to be retrieved.
     * @return List of operations associated with the specified customer.
     */
	@Query("SELECT o FROM Operation o JOIN o.account a JOIN a.customer c WHERE c.id = :customerId")
    List<Operation> findOperationsByCustomerId(@Param("customerId") Long customerId);
}
