package fr.tao.bankaccount.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.tao.bankaccount.util.MessageUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a customer entity with basic information 
 * and a list of associated accounts.
 * 
 * @see Account
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Entity
@Table(name = "customers")
@Data @NoArgsConstructor @AllArgsConstructor
public class Customer {
	
	/**
     * The unique identifier for the customer.
     */
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
     * The name of the customer.
     * Constraints:
     * - Must not be null or empty.
     * - Must be between 2 and 20 characters in length.
     * - Must match the specified regular expression pattern.
     */
	@NotBlank(message = MessageUtil.CUSTOMER_NAME_NOT_NULL_EMPTY)
    @Size(min = 2, max = 20, message = MessageUtil.CUSTOMER_NAME_NOT_VALID_SIZE)
	@Pattern(regexp = MessageUtil.CUSTOMER_NAME_REGEXP, message = MessageUtil.CUSTOMER_NAME_NOT_VALID_PATTERN)
	private String name;
	
	/**
     * The email address of the customer.
     * Constraints:
     * - Must not be null or empty.
     * - Must be a maximum of 30 characters in length.
     * - Must match the specified regular expression pattern.
     */
	@NotBlank(message = MessageUtil.CUSTOMER_EMAIL_NOT_NULL_EMPTY)
    @Size(max = 30, message = MessageUtil.CUSTOMER_EMAIL_NOT_VALID_SIZE)
	@Email(regexp = MessageUtil.CUSTOMER_EMAIL_REGEXP, message = MessageUtil.CUSTOMER_EMAIL_NOT_VALID_PATTERN)
	private String email;
	
	/**
     * The list of accounts associated with the customer.
     */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)	
	@OneToMany(mappedBy = "customer")
	private List<Account> accounts = new ArrayList<>();
	
	/**
	 * Constructs a new customer with the given attributes.
	 *
	 * @param id    The unique identifier for the customer.
	 * @param name  The name of the customer.
	 * @param email The email address of the customer.
	 */
	public Customer(Long id, String name, String email) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
	}
	
}
