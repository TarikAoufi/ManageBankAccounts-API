package fr.tao.bankaccount.dto.account;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a DTO (Data Transfer Object) for current account
 * details, extending the {@link AccountDetailsDto} class.
 * 
 * It provides information about a current account, including its overdraft
 * limit.
 * 
 * @see AccountDetailsDto
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CurrentAccountAllDto extends AccountDetailsDto {
	
	/**
	 * The overdraft limit of the current account.
	 */
	private BigDecimal overdraftLimit;

}
