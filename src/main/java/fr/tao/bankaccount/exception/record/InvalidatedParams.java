package fr.tao.bankaccount.exception.record;

import lombok.Builder;

/**
 * A record representing invalidated parameters due to constraint violations.
 *
 * @param cause     The cause of the constraint violation.
 * @param attribute The attribute or property associated with the constraint violation.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Builder
public record InvalidatedParams(String cause, String attribute) {}