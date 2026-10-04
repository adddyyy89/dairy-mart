package com.dairymart.dairyappserver.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger UI at /swagger-ui.html and OpenAPI JSON at /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dairyMartOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dairy Mart API")
                        .version("1.1")
                        .description("""
                                Backend for AdminLTE and the Flutter salesman/retailer apps.

                                Authenticate with HTTP Basic using phone number and password.
                                Validation errors return JSON: { "message": "..." }.
                                Order status ids: 1 NEW, 2 CONFIRMED, 3 REJECTED, 4 DISPATCHED, 5 DELIVERED, 6 RETURNED, 7 CANCELLED.
                                User type ids: 1 admin, 2 salesman, 3 retailer.
                                """)
                        .contact(new Contact().name("Dairy Mart")))
                .servers(List.of(new Server().url("/").description("This server")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .name("basicAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Phone number as username, account password as password.")));
    }

    @Bean
    public GroupedOpenApi adminGroup() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("AdminLTE")
                .pathsToMatch("/admin/**", "/inventory/**", "/crate/**", "/notification/**",
                        "/exceldump/**", "/branch/**", "/shop/**", "/salesmantoretail/**",
                        "/retailorder/**", "/user/**", "/product/**", "/ledger/**", "/tracking/**")
                .build();
    }

    @Bean
    public GroupedOpenApi appGroup() {
        return GroupedOpenApi.builder()
                .group("apps")
                .displayName("Flutter apps")
                .pathsToMatch("/auth/**", "/user/**", "/usertype/**", "/product/**", "/retailorder/**",
                        "/ledger/**", "/salesman/**", "/retailer/**", "/tracking/**", "/address/**",
                        "/crate/**", "/notification/**")
                .build();
    }
}
