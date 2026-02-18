package com.servientrega.locker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lockerOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Servientrega Locker API")
                .description("API REST para sistema de casilleros inteligentes")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Servientrega")
                    .email("soporte@servientrega.com")));
    }
}
