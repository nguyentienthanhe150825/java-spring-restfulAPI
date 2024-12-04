package vn.hoidanit.jobhunter.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // cho phép các URL nào có thể kết nối tới backend
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));

        // các method nào đc kết nối
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed methods
        
        // các phần header được phép gửi lên
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        
        // gửi kèm cookies hay không
        configuration.setAllowCredentials(true);

        // thời gian pre-flight request có thể cache (tính theo seconds)
        configuration.setMaxAge(3600L);
        // How long the response from a pre-flight request can be cached by clients

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // cấu hình cors cho tất cả api
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Link Tham khảo Spring:
    // https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html
}
