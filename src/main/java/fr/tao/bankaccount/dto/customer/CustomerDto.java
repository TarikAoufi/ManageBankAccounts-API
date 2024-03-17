package fr.tao.bankaccount.dto.customer;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.util.MessageUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a data transfer object (DTO) for Customer entities.
 * 
 * @see AccountDto
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class CustomerDto {
	
	/** The ID of the customer. */
	private Long id;
	
	/** The name of the customer. */
	@NotBlank(message = MessageUtil.CUSTOMER_NAME_NOT_NULL_EMPTY)
    @Size(min = 2, max = 20, message = MessageUtil.CUSTOMER_NAME_NOT_VALID_SIZE)
	@Pattern(regexp = MessageUtil.CUSTOMER_NAME_REGEXP, message = MessageUtil.CUSTOMER_NAME_NOT_VALID_PATTERN)
	private String name;
	
	/** The email of the customer. */
	@NotBlank(message = MessageUtil.CUSTOMER_EMAIL_NOT_NULL_EMPTY)
    @Size(max = 30, message = MessageUtil.CUSTOMER_EMAIL_NOT_VALID_SIZE)
	@Email(regexp = MessageUtil.CUSTOMER_EMAIL_REGEXP, message = MessageUtil.CUSTOMER_EMAIL_NOT_VALID_PATTERN)
	private String email;
	
	/** List of account DTOs associated with the customer. */
	@JsonInclude(Include.NON_NULL)
	private List<AccountDto> accountDtos;
	
	/**
     * Constructs a new CustomerDto with the given ID, name, and email.
     * 
     * @param id    The ID of the customer.
     * @param name  The name of the customer.
     * @param email The email of the customer.
     */
	public CustomerDto(Long id, String name, String email) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
	}
	
}
