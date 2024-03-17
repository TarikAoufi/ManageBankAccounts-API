package fr.tao.bankaccount.dto.account;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a DTO (Data Transfer Object) for savings account details.
 * Extends {@link AccountDetailsDto}.
 * 
 * @see AccountDetailsDto
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SavingsAccountAllDto extends AccountDetailsDto {
	
	/**
     * The interest rate associated with the savings account.
     */
	private BigDecimal interestRate;
	
}
