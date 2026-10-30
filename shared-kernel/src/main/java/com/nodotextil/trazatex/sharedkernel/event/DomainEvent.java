package com.nodotextil.trazatex.sharedkernel.event;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID eventId();

    /** Nombre del evento en pasado, igual que el catálogo (por ejemplo {@code BatchRegistered}). */
    String type();

    String aggregateId();

    int version();

    Instant occurredAt();

    Object payload();
}
