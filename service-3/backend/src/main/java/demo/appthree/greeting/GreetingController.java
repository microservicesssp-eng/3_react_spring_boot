package demo.appthree.greeting;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/greetings")
@CrossOrigin(origins = "http://localhost:5174")
public class GreetingController {
    private final SqliteGreetingRepository repository;

    public GreetingController(SqliteGreetingRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<GreetingRecord> getGreetings() {
        return repository.findAll();
    }
}
