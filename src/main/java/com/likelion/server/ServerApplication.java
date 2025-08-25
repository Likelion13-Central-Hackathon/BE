package com.likelion.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Bean
	public CommandLineRunner dbConnectionCheck(
			@Value("${spring.datasource.url}") String dbUrl,
			@Value("${spring.datasource.username}") String dbUsername
	) {
		return args -> {
			log.trace("=== Database Connection Info ===");
            log.trace("URL: {}", dbUrl);
            log.trace("Username: {}", dbUsername);
			log.trace("================================");
		};
	}
}
