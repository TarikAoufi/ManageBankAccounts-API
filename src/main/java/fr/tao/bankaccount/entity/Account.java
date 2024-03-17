package fr.tao.bankaccount.entity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import fr.tao.bankaccount.enums.AccountStatus;
import fr.tao.bankaccount.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an account in the system.
 *
 * <p>
 * This class is used to store information about a financial account, including
 * its unique identifier, balance, creation and modification timestamps, status,
 * associated customer, and a list of operations.
 * </p>
 *
 * <p>
 * This version of the {@code Account} class is mutable, meaning that its
 * properties can be modified after the object is instantiated. It uses Lombok
 * annotations for getters, setters, and constructors.
 * </p>
 *
 * @see AccountStatus
 * @see Customer
 * @see Operation
 *
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING, length = 20)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	
	/**
     * Unique identifier for the account. 
     */
	@Id
	private String id;
	
	/**
     * The balance associated with the account. 
     */
	private BigDecimal balance;
	
	/**
     * The date and time when the account was created.
     */
	@Column(name = "created_on", updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private ZonedDateTime createdOn;
	
	/**
     * The status of the account.
     */
	@Enumerated(EnumType.STRING)
	private AccountStatus status;
	
	/**
     * The date and time when the account was last modified.
     */
	@Column(name = "modified_on")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private ZonedDateTime modifiedOn;
	
	/**
     * The customer associated with the account.
     */
	@ManyToOne
	@JoinColumn(name = "customer_id")
//	@Valid
	private Customer customer;
	
	/**
     * List of operations associated with the account.
     */
	@OneToMany(mappedBy = "account")
	private List<Operation> operations = new ArrayList<>();
	
	/**
     * Callback method executed before persisting a new account.
     * Generates a random UUID, sets the creation date, and initializes the status.
     */
	@PrePersist
	private void prePersist() {
		this.setId(UUID.randomUUID().toString());
		this.createdOn = ZonedDateTime.now();
		this.status = AccountStatus.CREATED;
	}
	
	/**
     * Callback method executed before updating an existing account.
     * Updates the modification date.
     */
	@PreUpdate
	public void preUpdate() {
		modifiedOn = ZonedDateTime.now();
	}
	
	/**
     * Get the account type using the discriminator value.
     *
     * @return The account type based on the discriminator value.
     */
	public AccountType getAccountType() {
		return AccountType.valueOf(this.getClass().getAnnotation(DiscriminatorValue.class).value());
	}

}
