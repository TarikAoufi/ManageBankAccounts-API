package fr.tao.bankaccount.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import fr.tao.bankaccount.entity.Account;
import fr.tao.bankaccount.entity.Operation;

/**
 * This interface represents a repository for managing {@link Account} entities.
 * It extends the {@link JpaRepository} interface for basic CRUD operations.
 * The repository is annotated with {@link RepositoryRestResource} to expose RESTful endpoints.
 *
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@RepositoryRestResource
public interface AccountRepository extends JpaRepository<Account, String> {
	
	/**
     * Finds a list of accounts associated with the given customer ID.
     *
     * @param customerId The ID of the customer.
     * @return A list of accounts associated with the specified customer ID.
     */
    List<Account> findByCustomerId(Long customerId);
    
    /**
     * Finds an account with customer and operations information by its ID.
     *
     * @param accountId The ID of the account.
     * @return An optional containing the account with customer and operations information.
     */
    // @Query("SELECT a FROM Account a JOIN FETCH a.customer c LEFT JOIN FETCH a.operations o WHERE a.id = :accountId")
    Optional<Account> findAccountWithCustomerAndOperationsById(String accountId);
    
    /**
     * Finds all accounts with customer and operations information.
     *
     * @return A list of accounts with customer and operations information.
     */
    @Query("SELECT distinct a FROM Account a LEFT JOIN FETCH a.customer LEFT JOIN FETCH a.operations o order by o  asc ")
    List<Account> findAllWithCustomerAndOperations();
    
    /**
     * Finds operations associated with an account, ordered by operation date in descending order.
     *
     * @param accountId The ID of the account.
     * @param pageable   The pagination information.
     * @return A page of operations associated with the specified account ID.
     */
    @Query("SELECT o FROM Operation o WHERE o.account.id = :accountId ORDER BY o.operationDate DESC")
    public Page<Operation> findOperationsByAccountIdOrderByOperationDateDesc(String accountId, Pageable pageable);	
    
    /**
     * Finds and returns a page of operations associated with the account, sorted by date in descending order.
     *
     * @param accountId The ID of the account.
     * @param pageable   The pagination information.
     * @return A page of sorted operations associated with the specified account ID.
     */
    default Page<Operation> findSortedOperationsByAccountId(String accountId, Pageable pageable) {
        var account = findById(accountId).orElseThrow(); 
        // Sort operations by descending date
        var sortedOperations = account.getOperations().stream()
                .sorted(Comparator.comparing(Operation::getOperationDate).reversed())
                .toList();

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedOperations.size());
        return new PageImpl<>(sortedOperations.subList(start, end), pageable, sortedOperations.size());
    }

    
}
