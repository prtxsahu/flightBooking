package com.flightbooking.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testPostgreSQLConnection() throws SQLException {
        // Test DataSource injection
        assertThat(dataSource).isNotNull();
        
        // Test JdbcTemplate injection
        assertThat(jdbcTemplate).isNotNull();
        
        // Test actual connection
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isValid(5)).isTrue();
            
            // Test simple query
            String result = jdbcTemplate.queryForObject("SELECT 'Hello PostgreSQL'", String.class);
            assertThat(result).isEqualTo("Hello PostgreSQL");
        }
    }
}
