package com.nodotextil.trazatex.production.domain.model;

public interface TransformationStrategy {

    TransformationType type();

    /** Lanza {@code ValidationException} (400) si el balance incumple una regla propia del tipo. */
    void validate(BalanceSummary balance);
}
