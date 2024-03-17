package fr.tao.bankaccount.dto.operation;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing a transfer operation between two
 * accounts. It contains the details of the source account operation and the
 * target account operation.
 * 
 * @see OperationDto
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {
	
	/**
     * The operation details for the source account.
     *
     * @see OperationDto
     */
	@Valid
	private OperationDto sourceAccountOperationDto;	
	
	/**
     * The operation details for the target account.
     *
     * @see OperationDto
     */
	@Valid
	private OperationDto targetAccountOperationDto;

}
