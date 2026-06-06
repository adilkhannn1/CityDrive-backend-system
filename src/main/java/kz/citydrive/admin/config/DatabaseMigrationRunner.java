package kz.citydrive.admin.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DatabaseMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        jdbcTemplate.execute(
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS is_approved BOOLEAN DEFAULT FALSE");
        jdbcTemplate.execute("UPDATE users SET is_approved = FALSE WHERE is_approved IS NULL");
        jdbcTemplate.execute(
                "ALTER TABLE pending_registrations ADD COLUMN IF NOT EXISTS role VARCHAR(32) DEFAULT 'RESIDENT'");
        jdbcTemplate.execute(
                "UPDATE pending_registrations SET role = 'RESIDENT' WHERE role IS NULL");
        jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(512)");
        jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS lang VARCHAR(8) DEFAULT 'ru'");
        jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS device_token VARCHAR(512)");
        jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS device_type VARCHAR(32)");
        jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP");
        jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP");
        jdbcTemplate.execute("UPDATE users SET lang = 'ru' WHERE lang IS NULL");
        jdbcTemplate.execute("UPDATE users SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL");
        jdbcTemplate.execute("UPDATE users SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL");
        jdbcTemplate.execute(
                "UPDATE documents SET is_active = TRUE WHERE is_active IS NULL");
        jdbcTemplate.execute("UPDATE documents SET is_active = TRUE WHERE is_active = FALSE");
        jdbcTemplate.execute(
                "ALTER TABLE road_marks ADD COLUMN IF NOT EXISTS controller_comment VARCHAR(2000)");
        jdbcTemplate.execute("ALTER TABLE road_marks ADD COLUMN IF NOT EXISTS accepted_at TIMESTAMP");
    }
}
