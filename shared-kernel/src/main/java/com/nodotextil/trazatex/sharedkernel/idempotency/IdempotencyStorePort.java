package com.nodotextil.trazatex.sharedkernel.idempotency;

import java.time.Instant;
import java.util.Optional;

public interface IdempotencyStorePort {

    /** @return {@code true} si la clave era nueva y quedó reservada por la transacción actual. */
    boolean tryReserve(String key, String userId, String requestHash, Instant createdAt);

    Optional<IdempotencyRecord> find(String key);

    /** Guarda la respuesta de una clave reservada en esta transacción. {@code responseBody} es JSON o {@code null}. */
    void complete(String key, int responseStatus, String responseBody);

    void delete(String key);

    /** Para el job de limpieza de cada servicio (las claves caducan a las 24 h). */
    int deleteCreatedBefore(Instant cutoff);
}
