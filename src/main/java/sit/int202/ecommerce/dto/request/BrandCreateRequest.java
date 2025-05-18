
package sit.int202.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BrandCreateRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 30, message = "Name must be at most 30 characters")
    private String name;

    @Size(max = 255, message = "Website URL is too long")
    private String websiteUrl;

    private String countryOfOrigin;

    private Boolean isActive;

    public void normalize() {
        if (name != null) name = name.trim();
        if (websiteUrl != null) websiteUrl = websiteUrl.trim();
        if (countryOfOrigin != null) countryOfOrigin = countryOfOrigin.trim();
    }
}

