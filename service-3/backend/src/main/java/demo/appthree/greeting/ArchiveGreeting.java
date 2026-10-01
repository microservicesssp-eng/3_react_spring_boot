package demo.appthree.greeting;

import java.time.Instant;
import java.util.UUID;

public record ArchiveGreeting(Long id, UUID eventId, String name, String message,
                              Instant createdAt, Instant receivedAt) {
}
