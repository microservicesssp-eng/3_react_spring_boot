package demo.appone.greeting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GreetingDeliveryRepository extends JpaRepository<GreetingDelivery, Long> {
    List<GreetingDelivery> findTop50ByDeliveredAtIsNullOrderByIdAsc();
}
