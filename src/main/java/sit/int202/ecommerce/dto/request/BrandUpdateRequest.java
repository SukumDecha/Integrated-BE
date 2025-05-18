package sit.int202.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Data
public class BrandUpdateRequest {

    @NotNull(message = "Brand name must not be null")
    @NotBlank(message = "Brand name must not be blank")
    private String name;

    @URL(message = "Brand website URL must be a valid URL")
    private String websiteUrl;

    @Length(min = 1, max = 50, message = "Brand country of origin must be between 1 and 50 characters")
    private String countryOfOrigin;

    private Boolean isActive;
}
