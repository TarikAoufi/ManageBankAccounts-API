package fr.tao.bankaccount.dto.operation;

import java.math.BigDecimal;

import fr.tao.bankaccount.enums.OperationType;
import fr.tao.bankaccount.validation.ValidDestinationAccountId;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * {@code OperationRequestDto} represents a data transfer object (DTO) for
 * encapsulating operation request details.
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
public class OperationRequestDto {

	/**
	 * The account ID.
	 */
	private String accountId;

	/**
	 * The source account ID.
	 */
	private String sourceAccountId;

	/**
	 * The ID of the target account for transfer operations. Should adhere to the
	 * format specified by {@link ValidDestinationAccountId}.
	 */
	@ValidDestinationAccountId
	private String targetAccountId;

	/**
	 * The amount for the operation. 
	 * Must be a positive value greater than or equal to 0.01.
	 */
	@DecimalMin(value = "0.01", message = "Amount should be strictly positive and at least 0.01")
	private BigDecimal amount;

	/**
	 * The type of operatio (e.g., deposit, withdrawal, transfer).
	 * 
	 * @see OperationType
	 */
	private OperationType operationType;

}
