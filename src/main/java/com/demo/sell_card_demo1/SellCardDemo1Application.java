package com.demo.sell_card_demo1;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.RepositoryDefinition;

@SpringBootApplication
@EnableJpaRepositories

@OpenAPIDefinition(info = @Info(title = "MyPlatform API", version = "1.0", description = "API for MyPlatform application"))
@SecurityScheme(name = "api",scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class SellCardDemo1Application {

	public static void main(String[] args) {
		SpringApplication.run(SellCardDemo1Application.class, args);
	}

}

