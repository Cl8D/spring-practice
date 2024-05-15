package hello.config;

import hello.EnableTestEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@EnableTestEnvironment
@RequiredArgsConstructor
@Slf4j
public class DbConfigTest {

    private final DataSource dataSource;
    private final TransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    @Test
    void checkBean() {
        log.info("dataSource: {}", dataSource);
        log.info("transactionManager: {}", transactionManager);
        log.info("jdbcTemplate: {}", jdbcTemplate);

        assertThat(dataSource).isNotNull();
        assertThat(transactionManager).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
    }
}
