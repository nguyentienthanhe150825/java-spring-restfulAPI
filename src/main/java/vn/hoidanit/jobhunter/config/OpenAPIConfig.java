package vn.hoidanit.jobhunter.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {
    
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
    private Server createServer(String url, String description) {
        Server server = new Server();
        server.setUrl(url);
        server.setDescription(description);
        return server;
    }
    private Contact createContact() {
        return new Contact()
                .email("nguyenthanh2542001.work@gmail.com")
                .name("Nguyễn Thành")
                .url("https://www.facebook.com/nguyen.thanh.280894/?locale=vi_VN");
    }
    private License createLicense() {
        return new License()
                .name("Swagger Document")
                .url("https://springdoc.org/#getting-started");
    }
    private Info createApiInfo() {
        return new Info()
                .title("Job Hunter API")
                .version("1.0")
                .contact(createContact())
                .description("This API exposes all endpoints (job hunter)")
                .termsOfService("https://www.google.com.vn/?hl=vi")
                .license(createLicense());
    }
    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(List.of(
                        createServer("http://localhost:8080", "Server URL in Development environment"),
                        createServer("https://hoidanit.vn", "Server URL in Production environment")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }
}

// Document: https://springdoc.org/#getting-started
// Config Bearer Token Swagger: https://www.baeldung.com/spring-boot-swagger-jwt
// Config Swagger: https://www.bezkoder.com/spring-boot-swagger-3/