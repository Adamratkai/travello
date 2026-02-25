package com.travello.authservice.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI travelloOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("Travello Auth Service")
                        .description("Handles user registration, login and JWT token validation"));
    }
}
