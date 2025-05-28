package sit.int202.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(resolveAllowedOrigins())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Origin", "Accept", "Content-Type", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }

    private String[] resolveAllowedOrigins() {
        if ("*".equals(allowedOrigins)) {
            return new String[]{"*"};
        } else {
            return allowedOrigins.split("\\s*,\\s*");
        }
    }
}