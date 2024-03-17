package fr.tao.bankaccount.util;

/**
 * Utility class containing static final string constants for various messages
 * used in the application.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024 
 */
public final class MessageUtil {
	
	// Messages for model validator 
	public static final String CUSTOMER_NAME_NOT_NULL_EMPTY = "Name cannot be null or empty.";
	public static final String CUSTOMER_NAME_NOT_VALID_SIZE = "Name must be between 2 and 20 characters.";
	public static final String CUSTOMER_NAME_REGEXP = "^[a-zA-Z]+$";
	public static final String CUSTOMER_NAME_NOT_VALID_PATTERN = "Name should only contain alphabetic characters.";
	public static final String CUSTOMER_EMAIL_NOT_NULL_EMPTY = "Email cannot be null or empty.";
	public static final String CUSTOMER_EMAIL_NOT_VALID_SIZE = "Email cannot exceed 30 characters.";
	public static final String CUSTOMER_EMAIL_REGEXP = "^(?=.{2,30}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,3})$";
	public static final String CUSTOMER_EMAIL_NOT_VALID_PATTERN = "Email should be valid.";
	
	public static final String ACCOUNT_ID_NOT_NULL_EMPTY = "ID cannot be null or empty.";
	public static final String ACCOUNT_ID_REGEXP = "[a-fA-F0-9]{8}-(?:[a-fA-F0-9]{4}-){3}[a-fA-F0-9]{12}";
	public static final String ACCOUNT_ID_NOT_VALID_PATTERN = "Destination account ID should be a valid UUID";
	public static final String ACCOUNT_BALANCE_NOT_NULL_EMPTY = "Balance cannot be null or empty.";
	public static final String ACCOUNT_BALANCE_REGEXP = "^[0-9]+(\\.[0-9]{1,2})?$";
	public static final String ACCOUNT_BALANCE_NOT_VALID_PATTERN = "Balance should have 1 or 2 decimal places.";
	
	public static final String OPERATION_AMOUNT_REGEXP = "^\\d+(\\.\\d{1,2})?$";
	public static final String OPERATION_AMOUNT_NOT_VALID_PATTERN = "Amount should have at most two decimal places.";
	
	
	// Exception messages for service layer
	public static final String CUSTOMER_NOT_FOUND = "Customer not found with ID : ";
	public static final String CUSTOMERS_NO_RESULT = "No customers found.";
	public static final String CUSTOMERS_BY_NAME_NO_RESULT = "No customers found with the given name : ";
	public static final String CUSTOMER_VALIDATION_FAILED = "Validation failed for customer : {}";
	public static final String CUSTOMER_SUCCESS_DELETE = "Customer successfully deleted.";
	
	public static final String ACCOUNT_NOT_FOUND = "Could not find account with ID : ";
	public static final String ACCOUNTS_NO_RESULT= "No accounts found.";
	public static final String ACCOUNT_NOT_FOUND_DETAIL = "Account not found: {}";
	public static final String ACCOUNT_TYPE_NOT_FOUND = "Account type not supported.";
	public static final String ACCOUNT_SUCCESS_DELETE =  "Account deleted successfully.";
	public static final String SAME_SOURCE_AND_TARGET_ACCOUNT_ERROR = "Source and target accounts cannot be the same";
	
	public static final String OPERATION_NOT_FOUND = "Could not find operation with ID : ";
	public static final String OPERATIONS_NO_RESULT = "No operations found.";
	public static final String OPERATION_FAILED = "Operation failed: {}";
	public static final String OPERATION_SUCCESS_DELETE = "Operation successfully deleted.";
    public static final String UNSUPPORTED_OPERATION_ERROR = "Unsupported operation type";
	
	public static final String BAD_REQUEST = "Bad request: {}";
	public static final String INVALID_REQUEST = "Invalid request.";
    public static final String INTERNAL_SERVER_ERROR = "An error has occurred.";
    
    public static final String SUCCESS_VALIDATION = "Validation successful for {}: {}";

	// API messages
    public static final String API_REST_DOC_TITLE = "The Bank Accounts API";
    public static final String API_REST_DOC_DESCRIPTION = "This is a documentation for our Banking REST API";
    
    
    // Private constructor to prevent instantiation
    private MessageUtil() {
        throw new AssertionError("Utility class - do not instantiate");
    }

}
