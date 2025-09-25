package com.redhat.mta.examples.springboot.quarkus.events;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User Event Consumer demonstrating Spring Kafka patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace @Component with @ApplicationScoped
 * - Replace @KafkaListener with @Incoming (Reactive Messaging)
 * - Replace manual acknowledgment with automatic acknowledgment
 * - Replace Spring Kafka headers with Quarkus Kafka metadata
 * - Replace batch processing with Quarkus reactive streams
 */
@Component
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

    /**
     * Single event consumer
     * In Quarkus: Replace with @Incoming method
     */
    @KafkaListener(topics = "user-events", groupId = "user-service-group")
    public void handleUserEvent(@Payload UserEvent event,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                               @Header(KafkaHeaders.OFFSET) long offset,
                               Acknowledgment acknowledgment) {
        
        logger.info("Received user event from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        logger.info("Processing user event: {}", event);
        
        try {
            // Process the event based on type
            switch (event.getEventType()) {
                case USER_CREATED:
                    handleUserCreated(event);
                    break;
                case USER_UPDATED:
                    handleUserUpdated(event);
                    break;
                case USER_DELETED:
                    handleUserDeleted(event);
                    break;
                case USER_ACTIVATED:
                    handleUserActivated(event);
                    break;
                case USER_DEACTIVATED:
                    handleUserDeactivated(event);
                    break;
                default:
                    logger.warn("Unknown event type: {}", event.getEventType());
            }
            
            // Manual acknowledgment
            acknowledgment.acknowledge();
            logger.debug("Successfully processed and acknowledged user event: {}", event.getEventType());
            
        } catch (Exception e) {
            logger.error("Error processing user event: {}", event, e);
            // In a real scenario, you might want to send to a dead letter queue
        }
    }

    /**
     * Batch event consumer
     * In Quarkus: Replace with Multi<T> reactive streams
     */
    @KafkaListener(topics = "user-events-batch", groupId = "user-batch-group")
    public void handleUserEventsBatch(List<ConsumerRecord<String, UserEvent>> records,
                                     Acknowledgment acknowledgment) {
        
        logger.info("Received batch of {} user events", records.size());
        
        try {
            for (ConsumerRecord<String, UserEvent> record : records) {
                UserEvent event = record.value();
                logger.debug("Processing batch event: {} from partition: {}, offset: {}", 
                        event.getEventType(), record.partition(), record.offset());
                
                processUserEventInBatch(event);
            }
            
            // Acknowledge the entire batch
            acknowledgment.acknowledge();
            logger.info("Successfully processed and acknowledged batch of {} events", records.size());
            
        } catch (Exception e) {
            logger.error("Error processing user events batch", e);
        }
    }

    /**
     * Consumer with error handling
     * In Quarkus: Use @Incoming with failure strategies
     */
    @KafkaListener(topics = "user-events-retry", groupId = "user-retry-group")
    public void handleUserEventWithRetry(@Payload UserEvent event,
                                        ConsumerRecord<String, UserEvent> record) {
        
        logger.info("Processing user event with retry logic: {}", event.getEventType());
        
        try {
            // Simulate processing that might fail
            if (event.getDetails() != null && event.getDetails().contains("error")) {
                throw new RuntimeException("Simulated processing error");
            }
            
            processUserEvent(event);
            logger.info("Successfully processed user event: {}", event.getEventType());
            
        } catch (Exception e) {
            logger.error("Failed to process user event: {} from partition: {}, offset: {}", 
                    event.getEventType(), record.partition(), record.offset(), e);
            
            // In Quarkus, you would configure retry/failure strategies in application.properties
            throw e; // This will trigger retry mechanism
        }
    }

    /**
     * Consumer for different event types on separate topics
     */
    @KafkaListener(topics = "user-created-events", groupId = "user-created-group")
    public void handleUserCreatedEvents(@Payload UserEvent event) {
        logger.info("Handling user created event specifically: {}", event);
        handleUserCreated(event);
    }

    @KafkaListener(topics = "user-deleted-events", groupId = "user-deleted-group")
    public void handleUserDeletedEvents(@Payload UserEvent event) {
        logger.info("Handling user deleted event specifically: {}", event);
        handleUserDeleted(event);
    }

    // Event processing methods
    
    private void handleUserCreated(UserEvent event) {
        logger.info("Processing user created event for user: {}", event.getUsername());
        // Send welcome email, create user profile, etc.
        simulateProcessing();
    }

    private void handleUserUpdated(UserEvent event) {
        logger.info("Processing user updated event for user: {}", event.getUsername());
        // Update search index, invalidate cache, etc.
        simulateProcessing();
    }

    private void handleUserDeleted(UserEvent event) {
        logger.info("Processing user deleted event for user: {}", event.getUsername());
        // Clean up user data, remove from search index, etc.
        simulateProcessing();
    }

    private void handleUserActivated(UserEvent event) {
        logger.info("Processing user activated event for user: {}", event.getUsername());
        // Enable user features, send activation email, etc.
        simulateProcessing();
    }

    private void handleUserDeactivated(UserEvent event) {
        logger.info("Processing user deactivated event for user: {}", event.getUsername());
        // Disable user features, log security event, etc.
        simulateProcessing();
    }

    private void processUserEvent(UserEvent event) {
        // Generic event processing
        logger.debug("Generic processing for event: {}", event.getEventType());
        simulateProcessing();
    }

    private void processUserEventInBatch(UserEvent event) {
        // Batch-specific processing (might be more efficient)
        logger.debug("Batch processing for event: {}", event.getEventType());
        simulateProcessing();
    }

    private void simulateProcessing() {
        try {
            // Simulate some processing time
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Processing interrupted");
        }
    }
}
