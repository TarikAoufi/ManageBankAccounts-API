package fr.tao.bankaccount.service.validation;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import fr.tao.bankaccount.util.MessageUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for performing entity validation using the provided Validator.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {

    @NonNull
    private final Validator validator;

    /**
     * Validates the provided entity using the configured validator.
     * Throws a {@code ConstraintViolationException} if validation fails.
     *
     * @param entity The entity to be validated.
     * @param <T>    The type of the entity.
     */
    public <T> void validateAndThrow(T entity) {
        // Perform validation using the configured validator
        Set<ConstraintViolation<T>> violations = validator.validate(entity);

        // Check if there are any validation violations
        if (!violations.isEmpty()) {
            log.warn("Validation failed for {}: {}", entity.getClass().getSimpleName(), entity);
            // Throw a ConstraintViolationException
            throw new ConstraintViolationException(new HashSet<>(violations));
        }
        log.info(MessageUtil.SUCCESS_VALIDATION, entity.getClass().getSimpleName(), entity);
    }
  
}
