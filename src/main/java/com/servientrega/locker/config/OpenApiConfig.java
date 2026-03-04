package com.servientrega.locker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI lockerOpenAPI() {
        return new OpenAPI()
            .servers(List.of(
                new Server().url(serverUrl).description("API Server")
            ))
            .info(new Info()
                .title("Servientrega Locker API")
                .description("API REST para sistema de casilleros inteligentes")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Servientrega")
                    .email("soporte@servientrega.com")));
    }
}
