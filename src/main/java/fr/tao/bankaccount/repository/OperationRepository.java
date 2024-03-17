package fr.tao.bankaccount.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import fr.tao.bankaccount.entity.Operation;

/**
 * Repository interface for managing {@link Operation} entities.
 * This interface extends {@link JpaRepository} to provide basic CRUD operations.
 *
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@RepositoryRestResource
public interface OperationRepository extends JpaRepository<Operation, Long> {
	
	/**
     * Finds the last operation associated with the given account ID.
     *
     * @param accountId The unique identifier of the account.
     * @return The last operation associated with the specified account ID, or {@code null} if none found.
     */
	public Operation findLastOperationByAccountId(String accountId);
		
}
