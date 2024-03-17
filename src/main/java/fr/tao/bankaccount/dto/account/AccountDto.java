package fr.tao.bankaccount.dto.account;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.tao.bankaccount.dto.customer.CustomerDto;
import fr.tao.bankaccount.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a data transfer object (DTO) for an account.
 *
 * @see AccountStatus
 * @see CustomerDto
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
	
	/** The unique identifier of the account. */
	private String id;
	
	/** The balance of the account. */
	private BigDecimal balance;
	
	/** The date and time when the account was created. */
	private ZonedDateTime createdOn;
	
	/** The status of the account. 
	 * @see AccountStatus
	 */
	private AccountStatus status;
	
	/** The date and time when the account was last modified. */
	@JsonInclude(Include.NON_NULL)
	private ZonedDateTime modifiedOn;
	
	/**
     * Information about the associated customer, if applicable.
     * @see CustomerDto
     */
	@JsonInclude(Include.NON_NULL)
	private CustomerDto customerDto;

}
