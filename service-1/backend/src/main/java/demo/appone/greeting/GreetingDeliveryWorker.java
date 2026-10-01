package demo.appone.greeting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
public class GreetingDeliveryWorker {
    private static final Logger logger = LoggerFactory.getLogger(GreetingDeliveryWorker.class);

    private final GreetingDeliveryRepository repository;
    private final GreetingForwarder forwarder;

    public GreetingDeliveryWorker(GreetingDeliveryRepository repository, GreetingForwarder forwarder) {
        this.repository = repository;
        this.forwarder = forwarder;
    }

    @Scheduled(fixedDelayString = "${services.service2.retry-delay-ms:3000}")
    public void deliverPendingGreetings() {
        for (GreetingDelivery delivery : repository.findTop50ByDeliveredAtIsNullOrderByIdAsc()) {
            try {
                forwarder.forward(delivery);
                delivery.markDelivered();
            } catch (RestClientException exception) {
                delivery.markFailed(exception.getMessage() == null
                        ? exception.getClass().getSimpleName()
                        : exception.getMessage());
                logger.warn("Не удалось доставить событие {} в сервис 2; повтор будет выполнен позже",
                        delivery.getEventId(), exception);
            }
            repository.save(delivery);
        }
    }
}
