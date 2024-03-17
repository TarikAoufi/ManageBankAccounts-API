package fr.tao.bankaccount.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a Current Account entity, a type of account that allows overdrafts.
 * Extends the {@link Account} class.
 * 
 * @see Account
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Entity
@DiscriminatorValue("CURRENT")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CurrentAccount extends Account {
	
	/**
     * The overdraft limit associated with the current account.
     */
	@Column(name="overdraft_limit")
	private BigDecimal overdraftLimit;

}
