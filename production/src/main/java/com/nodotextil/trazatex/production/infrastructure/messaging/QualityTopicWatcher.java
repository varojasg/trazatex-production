package com.nodotextil.trazatex.production.infrastructure.messaging;

import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * {@code quality.control} es un tópico de Quality (siguiente sprint) y production no crea tópicos ajenos. Un consumidor
 * suscrito a un tópico que no existe escribe un aviso {@code UNKNOWN_TOPIC_OR_PARTITION} cada segundo, sin parar. Por eso el
 * contenedor de ese tópico no arranca solo: este vigilante pregunta cada
 * {@code trazatex.messaging.consumer.quality.topic-check-interval} (15 s) si el tópico ya existe y, cuando aparece, lo arranca;
 * desde entonces no vuelve a preguntar. El grupo es nuevo, así que lee desde el principio ({@code earliest}).
 */
@Component
@ConditionalOnProperty(name = "trazatex.messaging.enabled", havingValue = "true", matchIfMissing = true)
class QualityTopicWatcher {

    private static final Logger log = LoggerFactory.getLogger(QualityTopicWatcher.class);

    private final KafkaListenerEndpointRegistry registry;
    private final KafkaAdmin kafkaAdmin;

    QualityTopicWatcher(KafkaListenerEndpointRegistry registry, KafkaAdmin kafkaAdmin) {
        this.registry = registry;
        this.kafkaAdmin = kafkaAdmin;
    }

    @Scheduled(initialDelay = 1000, fixedDelayString = "${trazatex.messaging.consumer.quality.topic-check-interval:PT15S}")
    void startConsumingWhenTheTopicExists() {
        MessageListenerContainer container = registry.getListenerContainer(ConsumedTopics.QUALITY_LISTENER_ID);
        if (container == null || container.isRunning()) {
            return;
        }
        if (topicExists(ConsumedTopics.QUALITY_CONTROL)) {
            log.info("El tópico {} ya existe: se empieza a consumir", ConsumedTopics.QUALITY_CONTROL);
            container.start();
        } else {
            log.debug("El tópico {} todavía no existe", ConsumedTopics.QUALITY_CONTROL);
        }
    }

    private boolean topicExists(String topic) {
        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            return admin.listTopics().names().get(10, TimeUnit.SECONDS).contains(topic);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            log.warn("No se pudo consultar si existe {}: {}", topic, e.toString());
            return false;
        }
    }
}
