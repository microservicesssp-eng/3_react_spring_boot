package demo.appone.greeting;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.UUID;

@Entity
public class GreetingDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID eventId;
    private String name;
    private String message;
    private Instant createdAt;
    private Instant deliveredAt;
    private int attempts;
    private String lastError;

    protected GreetingDelivery() {
    }

    public GreetingDelivery(Greeting greeting) {
        this.eventId = greeting.getEventId();
        this.name = greeting.getName();
        this.message = greeting.getMessage();
        this.createdAt = greeting.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void markDelivered() {
        deliveredAt = Instant.now();
        lastError = null;
    }

    public void markFailed(String error) {
        attempts++;
        lastError = error.length() > 1000 ? error.substring(0, 1000) : error;
    }
}
