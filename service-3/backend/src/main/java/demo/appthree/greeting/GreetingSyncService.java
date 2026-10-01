package demo.appthree.greeting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class GreetingSyncService {
    private static final Logger logger = LoggerFactory.getLogger(GreetingSyncService.class);
    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");

    private final RestClient restClient;
    private final SqliteGreetingRepository repository;

    public GreetingSyncService(
            RestClient.Builder builder,
            SqliteGreetingRepository repository,
            @Value("${services.archive.url}") String archiveUrl
    ) {
        this.restClient = builder.baseUrl(archiveUrl).build();
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartup() {
        syncFromArchive();
    }

    @Scheduled(fixedDelayString = "${services.sync.interval-ms:5000}")
    public void syncFromArchive() {
        try {
            List<ArchiveGreeting> greetings = restClient.get()
                    .uri("/api/greetings")
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<>() {});
            if (greetings == null) {
                throw new IllegalStateException("Сервис 2 вернул пустое тело ответа");
            }
            String recordedAt = ZonedDateTime.now(WARSAW).toOffsetDateTime().toString();
            greetings.forEach(greeting -> repository.saveIfAbsent(greeting, recordedAt));
        } catch (org.springframework.web.client.RestClientException exception) {
            logger.warn("Не удалось прочитать сообщения из сервиса 2; повтор будет выполнен позже",
                    exception);
        }
    }
}
