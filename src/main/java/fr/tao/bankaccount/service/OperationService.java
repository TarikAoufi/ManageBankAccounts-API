package fr.tao.bankaccount.service;

import fr.tao.bankaccount.dto.operation.OperationRequestDto;
import fr.tao.bankaccount.exception.OperationNotFoundException;

/**
 * Service interface for performing banking operations.
 * The OperationService interface defines methods for performing operations on accounts.
 * Implementing classes should provide the logic for handling various operations.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
public interface OperationService {
	
	/**
     * Perform a banking operation based on the provided request {@link OperationRequestDto}.
     *
     * @param request The OperationRequestDto containing necessary information for the operation.
     * @return An Object representing the result of the operation.
     * @throws Exception If there is an error during the operation.
     */
	public Object performOperation(OperationRequestDto request) throws Exception;
	
	/**
     * Deletes an operation with the specified ID.
     *
     * @param operationId The ID of the operation to delete.
     * @throws OperationNotFoundException if the operation with the specified ID is not found.
     */
	public void deleteOperation(Long operationId) throws OperationNotFoundException;
		
}
