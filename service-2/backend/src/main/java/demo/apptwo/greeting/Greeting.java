package demo.apptwo.greeting;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Greeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;
    private String name;
    private String message;
    private Instant createdAt;
    private Instant receivedAt;

    protected Greeting() {
    }

    public Greeting(UUID eventId, String name, String message, Instant createdAt) {
        this.eventId = eventId;
        this.name = name;
        this.message = message;
        this.createdAt = createdAt;
        this.receivedAt = Instant.now();
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

    public Instant getReceivedAt() {
        return receivedAt;
    }
}
