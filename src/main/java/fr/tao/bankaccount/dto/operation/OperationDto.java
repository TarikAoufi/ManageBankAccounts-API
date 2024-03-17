package fr.tao.bankaccount.dto.operation;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.enums.OperationType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an operation.
 * 
 * @see OperationType
 * @see AccountDto
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OperationDto {
	
	/** The unique identifier of the operation. */
	private Long id;
	
	/**
     * The amount of the operation.
     * Must be strictly positive and at least 0.01.
     */
	@DecimalMin(value = "0.01", message = "Amount should be strictly positive and at least 0.01")
	private BigDecimal amount;
	
	/**
     * The type of the operation.
     * Must not be null.
     */
	@NotNull(message = "Operation type must not be null")
	private OperationType operationType;
	
	/**
     * The date and time when the operation occurred.
     */
	private ZonedDateTime operationDate;
	
	/**
     * A description of the operation.
     */
	private String description;
	
	/**
     * The DTO representing the account associated with the operation.
     * Can be null.
     *
     * @see AccountDto
     */
	@JsonInclude(Include.NON_NULL)
	@Valid
	private AccountDto accountDto;

}
