package demo.appthree.greeting;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SqliteGreetingRepository {
    private final JdbcTemplate jdbcTemplate;

    public SqliteGreetingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createSchema() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS greetings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    source_id INTEGER NOT NULL UNIQUE,
                    name TEXT NOT NULL,
                    message TEXT NOT NULL,
                    recorded_at TEXT NOT NULL
                )
                """);
    }

    public void saveIfAbsent(ArchiveGreeting greeting, String recordedAt) {
        jdbcTemplate.update("""
                INSERT INTO greetings (source_id, name, message, recorded_at)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(source_id) DO NOTHING
                """,
                greeting.id(),
                greeting.name(),
                greeting.message(),
                recordedAt
        );
    }

    public List<GreetingRecord> findAll() {
        return jdbcTemplate.query(
                "SELECT id, source_id, name, message, recorded_at FROM greetings ORDER BY id DESC",
                this::mapGreeting
        );
    }

    private GreetingRecord mapGreeting(ResultSet resultSet, int rowNumber) throws SQLException {
        return new GreetingRecord(
                resultSet.getLong("id"),
                resultSet.getLong("source_id"),
                resultSet.getString("name"),
                resultSet.getString("message"),
                resultSet.getString("recorded_at")
        );
    }
}
