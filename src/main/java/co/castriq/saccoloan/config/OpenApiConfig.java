package co.castriq.saccoloan.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI saccoLoanEngineOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SACCO Loan Eligibility & Contribution Tracker API")
                .description("Tracks member savings and automates loan eligibility decisions for a SACCO")
                .version("1.0.0")
                .contact(new Contact().name("Castriq Technologies")));
    }
}
