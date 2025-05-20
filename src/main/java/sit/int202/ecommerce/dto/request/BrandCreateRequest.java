
package sit.int202.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Data
public class BrandCreateRequest {

    @NotNull(message = "Brand ID must not be null")
    @NotBlank(message = "Brand name must not be blank")
//    @Length(min = 1, max = 30, message = "Brand name must be between 1 and 100 characters")
    private String name;

//    @Length(max = 40, message = "Website URL is too long")
//    @URL(message = "Website URL must be a valid URL")
    private String websiteUrl;

    private String countryOfOrigin;

    private Boolean isActive;

    public void normalize() {
        if (name != null) name = name.trim();
        if (websiteUrl != null) websiteUrl = websiteUrl.trim();
        if (countryOfOrigin != null) countryOfOrigin = countryOfOrigin.trim();
    }
}

