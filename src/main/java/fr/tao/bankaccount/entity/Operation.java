package fr.tao.bankaccount.entity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import fr.tao.bankaccount.enums.OperationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Represents an operation entity that is mapped to the "operations" table in
 * the database. This class defines the structure and behavior of financial
 * operations.
 * 
 * @see OperationType
 * @see Account
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Entity
@Table(name = "operations")
@Data @NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Operation {
	
	/**
     * Unique identifier for the operation.
     */
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
     * The amount associated with the operation.
     */
	@DecimalMin(value = "0.01", message = "Amount should be strictly positive and at least 0.01")
	private BigDecimal amount;
	
	/**
     * The type of operation, represented by an enumerated type.
     */
	@NotNull(message = "Operation type must not be null")
	@Enumerated(EnumType.STRING)
	@Column(name="operation_type")
	private OperationType operationType;
	
	/**
     * The date and time when the operation occurred, stored in UTC format.
     */
	@Column(name="operation_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private ZonedDateTime operationDate;
	
	/**
     * A description providing additional details about the operation.
     */
	private String description;
	
	/**
     * The account to which the operation is associated.
     */
	@ManyToOne(optional=false)
	@JoinColumn(name = "account_id", nullable = false)
	@Valid
	private Account account;
	
	/**
     * Callback method annotated with @PrePersist to set the operationDate before persisting.
     */
	@PrePersist
	private void prePersist() {
		this.operationDate = ZonedDateTime.now();
	}
	
}
