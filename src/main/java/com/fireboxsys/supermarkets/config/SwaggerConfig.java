package com.fireboxsys.supermarkets.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI supermarketsApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Supermarket Chain API")
                        .version("1.0.0")
                        .description("Official documentation of the RESTful API for centralized management of branches, product inventory, and sales transactions.")
                        .contact(new Contact()
                                .name("Jonathan Aguirre - Lead Developer")
                                .email("jonathan.aguirrecoutlook.com")
                                .url("https://github.com/jonathanAguirre1999")));
    }
}
