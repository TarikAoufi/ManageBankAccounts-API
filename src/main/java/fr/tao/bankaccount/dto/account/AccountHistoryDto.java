package fr.tao.bankaccount.dto.account;

import java.util.List;

import fr.tao.bankaccount.dto.operation.OperationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a data transfer object (DTO) for account history information.
 * 
 * <p>This class encapsulates information about the account, pagination details,
 * and a list of operations associated with the account.</p>
 * 
 * @see AccountDto
 * @see OperationDto
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Getter @Setter @Builder 
@NoArgsConstructor @AllArgsConstructor
public class AccountHistoryDto {
	
	/**
	 * The account DTO containing account information.
	 * @see AccountDto
	 */
	private AccountDto accountDto;
	
	/** The current page number of the pagination. */
	private int currentPage;
	
	/** The total number of pages for the pagination. */
	private int totalPage;
	
	/** The size of each page in the pagination. */
	private int pageSize;
	
	/**
	 * The list of operation DTOs representing operations associated with the
	 * account.
	 * @see OperationDto
	 */
	private List<OperationDto> operationDtos;

}
