package com.devsu.clientes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI clientesOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("MS Clientes API")
                .version("1.0.0")
                .description("Microservicio de Clientes y Personas"));
    }
}
