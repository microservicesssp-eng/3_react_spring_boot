package demo.appone.greeting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.UUID;

@Component
public class GreetingForwarder {
    private final RestClient restClient;

    public GreetingForwarder(
            RestClient.Builder builder,
            @Value("${services.service2.url}") String serviceUrl
    ) {
        this.restClient = builder.baseUrl(serviceUrl).build();
    }

    public void forward(GreetingDelivery delivery) {
        restClient.post()
                .uri("/api/greetings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ForwardGreeting(
                        delivery.getEventId(),
                        delivery.getName(),
                        delivery.getMessage(),
                        delivery.getCreatedAt()
                ))
                .retrieve()
                .toBodilessEntity();
    }

    private record ForwardGreeting(UUID eventId, String name, String message, Instant createdAt) {
    }
}
