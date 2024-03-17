package fr.tao.bankaccount.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;

/**
 * {@code ValidDestinationAccountId} is a custom annotation used for validating
 * the format of destination account IDs. It is typically applied to fields or
 * methods to ensure that the specified account ID adheres to a specific
 * pattern. The validation is performed by the
 * {@link ValidDestinationAccountIdValidator} class.
 *
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024 
 */
@Documented
@Constraint(validatedBy = {ValidDestinationAccountIdValidator.class})
@Pattern(regexp = "[a-fA-F0-9]{8}-(?:[a-fA-F0-9]{4}-){3}[a-fA-F0-9]{12}",
        message = "Destination account ID should be a valid format")
@ReportAsSingleViolation
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDestinationAccountId {
	
	/**
     * Default error message for invalid destination account ID format.
     */
    String message() default "Destination account ID should be a valid format";
    
    /**
     * Groups to which this constraint belongs.
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload associated with this constraint.
     */
    Class<? extends Payload>[] payload() default {};
 }
