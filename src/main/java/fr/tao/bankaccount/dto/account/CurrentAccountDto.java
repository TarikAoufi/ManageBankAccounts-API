package fr.tao.bankaccount.dto.account;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Represents a data transfer object (DTO) for a current account.
 * 
 * Inherits from {@link AccountDto}.
 * 
 * @see AccountDto
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentAccountDto extends AccountDto {

	/**
	 * The overdraft limit of the current account.
	 */
	private BigDecimal overdraftLimit;

}
