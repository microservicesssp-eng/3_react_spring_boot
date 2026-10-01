package demo.appone.greeting;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Greeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID eventId;

    @NotBlank
    private String name;

    @NotBlank
    private String message;

    private Instant createdAt;

    protected Greeting() {
    }

    public Greeting(UUID eventId, String name, String message) {
        this.eventId = eventId;
        this.name = name;
        this.message = message;
        this.createdAt = Instant.now();
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
}
