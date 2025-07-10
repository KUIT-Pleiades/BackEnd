package com.pleiades.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.ServletContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI getOpenAPI(ServletContext servletContext) {
        Server server = new Server().url(servletContext.getContextPath());
        return new OpenAPI()
                .servers(List.of(server))
                .components(new Components())
                .info(apiInfo());
    }
    private Info apiInfo() {

        return new Info()
                .title("Pleiades API")
                .description("Pleiades API")
                .version("1.0.0");
    }
}
