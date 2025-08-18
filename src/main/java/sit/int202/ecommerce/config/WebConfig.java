package sit.int202.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigins;

    @Value("${app.cors.enabled:true}")
    private boolean corsEnabled;

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir);
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (!corsEnabled) return;

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