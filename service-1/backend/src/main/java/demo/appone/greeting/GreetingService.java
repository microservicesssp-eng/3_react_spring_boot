package demo.appone.greeting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GreetingService {
    private final GreetingRepository greetingRepository;
    private final GreetingDeliveryRepository deliveryRepository;

    public GreetingService(
            GreetingRepository greetingRepository,
            GreetingDeliveryRepository deliveryRepository
    ) {
        this.greetingRepository = greetingRepository;
        this.deliveryRepository = deliveryRepository;
    }

    public List<Greeting> findAll() {
        return greetingRepository.findAll();
    }

    @Transactional
    public Greeting create(String name, String message) {
        Greeting greeting = greetingRepository.save(
                new Greeting(UUID.randomUUID(), name, message)
        );
        deliveryRepository.save(new GreetingDelivery(greeting));
        return greeting;
    }
}
