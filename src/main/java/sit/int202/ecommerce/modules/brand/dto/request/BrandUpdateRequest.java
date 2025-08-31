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

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl != null ? websiteUrl.trim() : null;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin != null ? countryOfOrigin.trim() : null;
    }
}
