package fr.tao.bankaccount.exception.handler;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import fr.tao.bankaccount.exception.AccountNotFoundException;
import fr.tao.bankaccount.exception.CustomerNotFoundException;
import fr.tao.bankaccount.exception.ErrorResponse;
import fr.tao.bankaccount.exception.InsufficientBalanceException;
import fr.tao.bankaccount.exception.OperationFailedException;
import fr.tao.bankaccount.exception.OperationNotFoundException;
import fr.tao.bankaccount.exception.record.InvalidatedParams;
import fr.tao.bankaccount.util.MessageUtil;
import fr.tao.bankaccount.validation.UUIDValidator.InvalidUUIDFormatException;
import jakarta.persistence.NoResultException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom global exception handler for handling various 
 * banking API exceptions and returning appropriate error responses.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler  {

	/**
     * Handles the CustomerNotFoundException and returns a ResponseEntity 
     * with a NOT FOUND status and a corresponding error response.
     *
     * @param ex The CustomerNotFoundException to handle.
     * @return ResponseEntity containing the error response for CustomerNotFoundException.
     */
	@ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException ex) {	
		// Create a standardized error response
		var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
		// Log the exception for further tracking
		log.error("CustomerNotFoundException handled: " + ex.getMessage());
		// Return the standardized error response along with an appropriate HTTP status
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
	
	/**
	 * Handles the AccountNotFoundException and returns a ResponseEntity 
     * with a NOT FOUND status and a corresponding error response.
     * 
	 * @param ex The AccountNotFoundException to handle.
	 * @return ResponseEntity containing the error response for AccountNotFoundException. 
	 */
	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex) {				
		var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());		
		log.error("AccountNotFoundException handled: " + ex.getMessage());		 
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
	
	/**
	 * Handles the OperationNotFoundException and returns a ResponseEntity 
     * with a NOT FOUND status and a corresponding error response.
     * 
	 * @param ex The OperationNotFoundException to handle
	 * @return ResponseEntity containing the error response for OperationNotFoundException
	 */
	@ExceptionHandler(OperationNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleOperationNotFoundException(OperationNotFoundException ex) {				
		var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());		
		log.error("OperationNotFoundException handled: " + ex.getMessage());		 
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
	
	/**
	 * Handles IllegalArgumentException globally for the API.
	 * Captures the IllegalArgumentException, logs the incident, and creates a standardized error response.
	 *
	 * @param ex The IllegalArgumentException that needs to be handled.
	 * @return A ResponseEntity containing a standardized error response and an appropriate HTTP status.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {	
		var errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());			    
		log.error("IllegalArgumentException handled: " + ex.getMessage());	     
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	/**
     * Handles the NoResultException and returns a ResponseEntity 
     * with an OK status and a corresponding error response.
     *
     * @param ex The NoResultException to handle.
     * @return ResponseEntity containing the error response for NoResultException.
     */
	@ExceptionHandler(NoResultException.class)
    public ResponseEntity<ErrorResponse> handleNoResultException(NoResultException ex) {		
		var errorResponse = new ErrorResponse(HttpStatus.OK, ex.getMessage());
		log.warn("NoResultException handled: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }
		
	/**
	 * Handles ConstraintViolationException and returns a ProblemDetail with details
	 * about the validation errors.
	 *
	 * @param e       The ConstraintViolationException that occurred.
	 * @param request The WebRequest associated with the request.
	 * @return A ProblemDetail containing information about the validation errors.
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ProblemDetail handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {
		// Extracting the set of constraint violations from the exception.
        Set<ConstraintViolation<?>> errors = e.getConstraintViolations();
        // Mapping the constraint violations to InvalidatedParams objects.
        List<InvalidatedParams> validationResponse = errors.stream()
            .map(err -> InvalidatedParams.builder()
                .cause(err.getMessage())
                .attribute(err.getPropertyPath().toString())
                .build()
            ).toList();
        
        // Creating a ProblemDetail with information about the validation failure.
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("invalidParams", validationResponse);
        log.error("ConstraintViolationException handled: " + e.getMessage());
      
        return problemDetail;
    }
	
	/**
	 * Handles MethodArgumentNotValidException by returning a ResponseEntity with a
	 * ProblemDetail containing information about the validation failure.
	 * 
	 * @param ex The MethodArgumentNotValidException thrown during validation.
	 * @return ResponseEntity containing a ProblemDetail with details about the
	 *         validation failure.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ProblemDetail> handleValidationErrors(MethodArgumentNotValidException ex) {
		// Retrieve validation errors
		List<InvalidatedParams> validationResponse = ex.getBindingResult().getFieldErrors()
				.stream()
				.map(err -> InvalidatedParams.builder()
						.cause(err.getDefaultMessage())
						.attribute(err.getField())
						.build())
				.toList();

		// Creating a ProblemDetail with information about the validation failure.
		var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
		problemDetail.setTitle("Validation Failed");
		problemDetail.setProperty("invalidParams", validationResponse);
		log.error("MethodArgumentNotValidException handled: " + ex.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(new HttpHeaders()).body(problemDetail);
    }	

	/**
	 * Exception handler method to handle InvalidUUIDFormatException. Returns a
	 * ResponseEntity containing a ProblemDetail with details of the validation
	 * failure.
	 *
	 * @param ex The InvalidUUIDFormatException to handle.
	 * @return A ResponseEntity with a ProblemDetail containing details of the
	 *         validation failure.
	 * @see InvalidUUIDFormatException
	 * @see ProblemDetail
	 */
	@ExceptionHandler(InvalidUUIDFormatException.class)
    public ResponseEntity<ProblemDetail> handleInvalidUUIDFormat(InvalidUUIDFormatException ex) {	
		List<InvalidatedParams> invalidParams = ex.getInvalidParams();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("invalidParams", invalidParams);

        log.error("InvalidUUIDFormatException handled: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

	/**
	 * This method handles various custom exceptions and returns an appropriate
	 * ResponseEntity with an ErrorResponse.
	 *
	 * @param ex The exception that needs to be handled.
	 * @return A ResponseEntity containing an ErrorResponse with details about the
	 *         handled exception.
	 */
	@ExceptionHandler({InsufficientBalanceException.class, OperationFailedException.class, UnsupportedOperationException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleCustomExceptions(Exception ex) {
		var errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	    log.error(ex.getClass().getSimpleName() + " handled: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
	
	/**
     * Handles generic exceptions and returns a ResponseEntity 
     * with an INTERNAL SERVER ERROR status and a standard error response.
     *
     * @param ex The Exception to handle.
     * @return ResponseEntity containing the standard error response for generic exceptions.
     */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {		
		var errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
				MessageUtil.INTERNAL_SERVER_ERROR);
		log.error("Unhandled exception in bank APIs: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
