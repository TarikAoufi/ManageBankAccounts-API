package fr.tao.bankaccount.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.tao.bankaccount.dto.operation.OperationDto;
import fr.tao.bankaccount.dto.operation.OperationRequestDto;
import fr.tao.bankaccount.dto.operation.TransferDto;
import fr.tao.bankaccount.exception.OperationNotFoundException;
import fr.tao.bankaccount.service.OperationService;
import fr.tao.bankaccount.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 * REST API for handling banking operations.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@RestController
@CrossOrigin(origins="http://localhost:4200", allowedHeaders="*")
@RequestMapping(value = "/api/bank/operations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class OperationRestAPI {
	
	/**
     * The operation service.
     */
	@NonNull 
	private final OperationService operationService;

	/**
	 * Handles deposit operation.
	 * 
	 * @param request The operation request.
	 * @return ResponseEntity containing the result of the deposit operation.
	 * @throws Exception if an error occurs during the deposit operation.
	 * @see OperationRequestDto
	 * @see OperationService#performOperation(OperationRequestDto)
	 */
	@PostMapping("/deposit")
	public ResponseEntity<Object> deposit(@Valid @RequestBody OperationRequestDto request) throws Exception {
		return processOperation(request, this::handleDepositAndWithdraw);
	}
	
	/**
	 * Handles withdraw operation.
	 * 
	 * @param request The operation request.
	 * @return ResponseEntity containing the result of the withdraw operation.
	 * @throws Exception if an error occurs during the withdraw operation.
	 * @see OperationRequestDto
	 * @see OperationService#performOperation(OperationRequestDto)
	 */
	@PostMapping("/withdraw")
	public ResponseEntity<Object> withdraw(@Valid @RequestBody OperationRequestDto request) throws Exception {
		return processOperation(request, this::handleDepositAndWithdraw);
	}
	
	/**
	 * Handles transfer operation.
	 * 
	 * @param request The operation request.
	 * @return ResponseEntity containing the result of the transfer operation.
	 * @throws Exception if an error occurs during the transfer operation.
	 * @see OperationRequestDto
	 * @see OperationService#performOperation(OperationRequestDto)
	 */
	@PostMapping("/transfer")
	public ResponseEntity<Object> transfer(@Valid @RequestBody OperationRequestDto request) throws Exception {
		return processOperation(request, this::handleTransfer);
	}
	
	/**
     * Delete an operation by their ID.
     *
     * @param operationId The ID of the operation to delete.
     * @return ResponseEntity<String>: A response entity with a message indicating the success of the deletion.
     * @throws OperationNotFoundException If the operation with the specified ID is not found.
     */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteOperation(@PathVariable("id") Long operationId) throws OperationNotFoundException {
		log.info("deleteOperation - REST request: Deleting operation with ID: {}", operationId);
		operationService.deleteOperation(operationId);		
		return ResponseEntity.ok().body(MessageUtil.OPERATION_SUCCESS_DELETE);
	}
	
	/**
	 * Handles the response for deposit and withdrawal operations.
	 * 
	 * @param result The result of the operation.
	 * @return ResponseEntity containing the appropriate response for the operation.
	 */
	private ResponseEntity<Object> handleDepositAndWithdraw(Object result) {
        if (result instanceof OperationDto operationDto) {
            return ResponseEntity.ok(operationDto);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MessageUtil.INTERNAL_SERVER_ERROR);
        }
    }
	
	/**
	 * Handles the response for transfer operations.
	 * 
	 * @param result The result of the operation.
	 * @return ResponseEntity containing the appropriate response for the operation.
	 */
    private ResponseEntity<Object> handleTransfer(Object result) {
        if (result instanceof TransferDto transferDto) {
            return ResponseEntity.ok(transferDto);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MessageUtil.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Process the operation request and handle the response.
     * 
     * @param request The operation request.
     * @param handler The response handler for the operation.
     * @return ResponseEntity containing the appropriate response for the operation.
     * @throws Exception If an error occurs during the operation.
     */
	private ResponseEntity<Object> processOperation(OperationRequestDto request, ResponseHandler handler)
			throws Exception {
		Object operationResult = operationService.performOperation(request);
		return handler.handleResponse(operationResult);
	}
	
	/**
	 * Functional interface to handle the response for different types of operations.
	 */
	@FunctionalInterface
	interface ResponseHandler {
		ResponseEntity<Object> handleResponse(Object result);
    }
	

}
