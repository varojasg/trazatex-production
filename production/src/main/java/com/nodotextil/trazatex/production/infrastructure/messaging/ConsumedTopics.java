package com.nodotextil.trazatex.production.infrastructure.messaging;

public final class ConsumedTopics {

    public static final String ORGACCESS_COMPANY = "orgaccess.company";
    public static final String QUALITY_CONTROL = "quality.control";

    /** Id del contenedor que lee {@link #QUALITY_CONTROL}: arranca solo cuando el tópico existe (ver {@code QualityTopicWatcher}). */
    public static final String QUALITY_LISTENER_ID = "production-quality-control";

    private ConsumedTopics() {
    }
}
