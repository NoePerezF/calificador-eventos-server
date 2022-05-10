package com.eventos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan("com")
@SpringBootApplication
@EnableJpaRepositories
public class EventosApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventosApplication.class, args);
	}
        

}
