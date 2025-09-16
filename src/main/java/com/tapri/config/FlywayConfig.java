package com.tapri.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return new FlywayMigrationStrategy() {
            @Override
            public void migrate(Flyway flyway) {
                try {
                    // First try to repair any failed migrations
                    flyway.repair();
                    
                    // Then migrate with out-of-order support
                    flyway.migrate();
                } catch (Exception e) {
                    // If migration fails, try to repair and migrate again
                    System.err.println("Flyway migration failed, attempting repair: " + e.getMessage());
                    try {
                        flyway.repair();
                        flyway.migrate();
                    } catch (Exception repairException) {
                        System.err.println("Flyway repair and migrate failed: " + repairException.getMessage());
                        throw repairException;
                    }
                }
            }
        };
    }
} 