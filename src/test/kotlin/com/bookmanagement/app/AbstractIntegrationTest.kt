package com.bookmanagement.app

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest
@Transactional
abstract class AbstractIntegrationTest {

    companion object {
        private val postgres = PostgreSQLContainer("postgres:17")
            .withDatabaseName("mydatabase")
            .withUsername("myuser")
            .withPassword("secret")
            .apply { start() }
 
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}