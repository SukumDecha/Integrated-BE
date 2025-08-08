package sit.int202.ecommerce.modules.brand.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandUpdateRequest {

    @NotBlank(message = "Brand name must not be blank")
//    @Length(min = 1, max = 30, message = "Brand name must be between 1 and 100 characters")
    private String name;

//    @URL(message = "Brand website URL must be a valid URL")
//    @Length(max = 40, message = "Brand website URL is too long")
    private String websiteUrl;

//    @Length(min = 1, max = 50, message = "Brand country of origin must be between 1 and 50 characters")
    private String countryOfOrigin;

    private Boolean isActive;

    public void normalize() {
        if (name != null) name = name.trim();
        if (websiteUrl != null) websiteUrl = websiteUrl.trim();
        if (countryOfOrigin != null) countryOfOrigin = countryOfOrigin.trim();
    }
}
