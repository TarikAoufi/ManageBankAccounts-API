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
 * Represents a savings account entity which extends the base Account class.
 * Savings accounts typically accrue interest over time.
 *
 * @see Account
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024 
 */
@Entity
@DiscriminatorValue("SAVINGS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SavingsAccount extends Account {
	
	/**
     * The interest rate associated with the savings account.
     */
	@Column(name="interest_rate")
	private BigDecimal interestRate;
	
}
