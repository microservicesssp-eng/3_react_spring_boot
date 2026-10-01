package demo.appthree.greeting;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SqliteInitializer implements ApplicationRunner {
    private final SqliteGreetingRepository repository;

    public SqliteInitializer(SqliteGreetingRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        repository.createSchema();
    }
}
