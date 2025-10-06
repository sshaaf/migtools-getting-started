package com.redhat.mta.examples.springboot.quarkus.events;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * User Event for Kafka messaging
 * 
 * Migration considerations:
 * - Event classes remain the same in Quarkus
 * - Jackson annotations work the same
 */
public class UserEvent {

    public enum EventType {
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        USER_ACTIVATED,
        USER_DEACTIVATED
    }

    private Long userId;
    private String username;
    private EventType eventType;
    private String details;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String source;

    // Constructors
    public UserEvent() {}

    public UserEvent(Long userId, String username, EventType eventType, String details) {
        this.userId = userId;
        this.username = username;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.source = "springboot-to-quarkus-migration";
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    @Override
    public String toString() {
        return "UserEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", eventType=" + eventType +
                ", details='" + details + '\'' +
                ", timestamp=" + timestamp +
                ", source='" + source + '\'' +
                '}';
    }
}


