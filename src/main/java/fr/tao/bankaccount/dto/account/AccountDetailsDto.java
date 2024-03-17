package fr.tao.bankaccount.dto.account;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.tao.bankaccount.dto.operation.OperationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object representing account details, including the account
 * information and associated operations.
 * 
 * @see AccountDto
 * @see OperationDto
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetailsDto {
	
	/**
     * The account information.
     */
	private AccountDto accountDto;
	
	/**
     * The list of operations associated with the account.
     */
	@JsonInclude(Include.NON_NULL)
	private List<OperationDto> operationDtos;

}
