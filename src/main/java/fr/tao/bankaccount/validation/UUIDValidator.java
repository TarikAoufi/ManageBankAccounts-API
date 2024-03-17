package fr.tao.bankaccount.validation;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import fr.tao.bankaccount.exception.record.InvalidatedParams;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for validating and converting UUIDs.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024 
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UUIDValidator {

    private static final Pattern UUID_PATTERN = Pattern.compile(
    		"[a-fA-F0-9]{8}-(?:[a-fA-F0-9]{4}-){3}[a-fA-F0-9]{12}");

    /**
     * Validates if the given string represents a valid UUID.
     *
     * @param id The string to be validated.
     * @return {@code true} if the string is a valid UUID, {@code false} otherwise.
     */
    public static boolean isValidUUID(String id) {
        boolean isValid = UUID_PATTERN.matcher(id).matches();
        log.debug("Validation result for UUID {}: {}", id, isValid ? "valid" : "invalid");
        return isValid;
    }

    /**
     * Validates and converts a string to a UUID.
     *
     * @param id The string to be validated and converted.
     * @return The UUID representation of the input string.
     * @throws InvalidUUIDFormatException If the input string is not a valid UUID.
     */
    public static UUID validateAndConvertToUUID(String id) throws InvalidUUIDFormatException {
    	if (!isValidUUID(id)) {
            String errorMessage = "Invalid UUID format: " + id;
            log.error(errorMessage);
            throw new InvalidUUIDFormatException(errorMessage);
        }
        UUID uuid = UUID.fromString(id);
        log.debug("UUID {} successfully validated and converted.", id);
        return uuid;
    }

    /**
     * Custom exception class for invalid UUID format. 
     */
    @SuppressWarnings("serial")
	public static class InvalidUUIDFormatException extends IllegalArgumentException {
    	
    	private String attribute;
    	
    	private List<InvalidatedParams> invalidParams;

        public InvalidUUIDFormatException(String message, List<InvalidatedParams> invalidParams) {
            super(message);
            this.invalidParams = invalidParams;
        }
        
        public InvalidUUIDFormatException(String message, List<InvalidatedParams> invalidParams, String attribute) {
            super(message);
            this.invalidParams = invalidParams;
            this.attribute = attribute;
        }

        /**
         * Constructs an InvalidUUIDFormatException with the specified detail message.
         *
         * @param message the detail message.
         */
        public InvalidUUIDFormatException(String message, String attribute) {
            super(message);
            this.attribute = attribute;
        }

        public InvalidUUIDFormatException(String message) {
        	super(message);
		}

		public String getAttribute() {
            return attribute;
        }
		
		public List<InvalidatedParams> getInvalidParams() {
	        return invalidParams;
	    }
    }
}