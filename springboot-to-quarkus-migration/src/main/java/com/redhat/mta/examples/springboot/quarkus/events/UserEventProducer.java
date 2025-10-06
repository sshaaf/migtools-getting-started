package com.redhat.mta.examples.springboot.quarkus.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * User Event Producer demonstrating Spring Kafka patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace @Service with @ApplicationScoped
 * - Replace @Autowired with @Inject
 * - Replace KafkaTemplate with @Channel Emitter (Reactive Messaging)
 * - Replace ListenableFuture with Uni<T> (Mutiny)
 * - Replace Spring Kafka configuration with Quarkus Kafka configuration
 */
@Service
public class UserEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventProducer.class);
    private static final String USER_EVENTS_TOPIC = "user-events";

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    /**
     * Send user event to Kafka topic
     * In Quarkus: Replace with @Channel Emitter
     */
    public void sendUserEvent(UserEvent event) {
        logger.info("Sending user event: {}", event);
        
        try {
            ListenableFuture<SendResult<String, UserEvent>> future = 
                    kafkaTemplate.send(USER_EVENTS_TOPIC, event.getUserId().toString(), event);
            
            future.addCallback(new ListenableFutureCallback<SendResult<String, UserEvent>>() {
                @Override
                public void onSuccess(SendResult<String, UserEvent> result) {
                    logger.info("Successfully sent user event: {} with offset: {}", 
                            event.getEventType(), result.getRecordMetadata().offset());
                }

                @Override
                public void onFailure(Throwable ex) {
                    logger.error("Failed to send user event: {}", event.getEventType(), ex);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error sending user event: {}", event, e);
        }
    }

    /**
     * Send user created event
     */
    public void sendUserCreatedEvent(Long userId, String username) {
        UserEvent event = new UserEvent(userId, username, UserEvent.EventType.USER_CREATED, 
                "User account created successfully");
        sendUserEvent(event);
    }

    /**
     * Send user updated event
     */
    public void sendUserUpdatedEvent(Long userId, String username, String details) {
        UserEvent event = new UserEvent(userId, username, UserEvent.EventType.USER_UPDATED, details);
        sendUserEvent(event);
    }

    /**
     * Send user deleted event
     */
    public void sendUserDeletedEvent(Long userId, String username) {
        UserEvent event = new UserEvent(userId, username, UserEvent.EventType.USER_DELETED, 
                "User account deleted");
        sendUserEvent(event);
    }

    /**
     * Send user activated event
     */
    public void sendUserActivatedEvent(Long userId, String username) {
        UserEvent event = new UserEvent(userId, username, UserEvent.EventType.USER_ACTIVATED, 
                "User account activated");
        sendUserEvent(event);
    }

    /**
     * Send user deactivated event
     */
    public void sendUserDeactivatedEvent(Long userId, String username) {
        UserEvent event = new UserEvent(userId, username, UserEvent.EventType.USER_DEACTIVATED, 
                "User account deactivated");
        sendUserEvent(event);
    }

    /**
     * Send batch user events
     */
    public void sendBatchUserEvents(java.util.List<UserEvent> events) {
        logger.info("Sending batch of {} user events", events.size());
        
        for (UserEvent event : events) {
            sendUserEvent(event);
        }
    }
}


