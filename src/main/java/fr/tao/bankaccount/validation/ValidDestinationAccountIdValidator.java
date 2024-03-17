package fr.tao.bankaccount.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code ValidDestinationAccountIdValidator} is the implementation of the
 * validation logic for the {@link ValidDestinationAccountId} annotation. It
 * ensures that a given destination account ID adheres to a specified pattern.
 *
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Slf4j
public class ValidDestinationAccountIdValidator implements ConstraintValidator<ValidDestinationAccountId, String> {

	private static final String UUID_PATTERN = "[a-fA-F0-9]{8}-(?:[a-fA-F0-9]{4}-){3}[a-fA-F0-9]{12}";

	/**
     * Initializes the validator.
     *
     * @param constraintAnnotation The annotation instance.
     */
	@Override
    public void initialize(ValidDestinationAccountId constraintAnnotation) {
		// Initialization logic, if needed
	}
	
	/**
     * Validates the target account ID.
     *
     * @param request  The operation request.
     * @param context  The validation context.
     * @return true if the validation passes, false otherwise.
     */
	@Override
    public boolean isValid(String targetAccountId, ConstraintValidatorContext context) {
		
		if (targetAccountId == null) {
			log.info("Validation skipped for deposit or withdrawal.");
			return true; // Do not validate the targetAccountId for DEPOSIT and WITHDRAWAL	
		}

		if (!Pattern.matches(UUID_PATTERN, targetAccountId)) {
			log.info("Validation failed for targetAccountId: " + targetAccountId);
						
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode("targetAccountId")
                   .addConstraintViolation();
            return false;
        }		
        return true;
    }
	
}
