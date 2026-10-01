package demo.apptwo.greeting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GreetingRepository extends JpaRepository<Greeting, Long> {
    Optional<Greeting> findByEventId(UUID eventId);
}
