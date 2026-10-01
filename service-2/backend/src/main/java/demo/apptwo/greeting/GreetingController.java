package demo.apptwo.greeting;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/greetings")
public class GreetingController {
    private final GreetingRepository repository;

    public GreetingController(GreetingRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Greeting> getGreetings() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Greeting createGreeting(@Valid @RequestBody GreetingRequest request) {
        return repository.findByEventId(request.eventId())
                .orElseGet(() -> repository.save(new Greeting(
                        request.eventId(),
                        request.name(),
                        request.message(),
                        request.createdAt()
                )));
    }

    public record GreetingRequest(
            @NotNull UUID eventId,
            @NotBlank String name,
            @NotBlank String message,
            @NotNull Instant createdAt
    ) {
    }
}
