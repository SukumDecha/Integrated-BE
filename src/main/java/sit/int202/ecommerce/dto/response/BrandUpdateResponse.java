package sit.int202.ecommerce.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Data
public class BrandUpdateResponse extends BrandResponse {

    private String websiteUrl;
    private String countryOfOrigin;
    private Boolean isActive;

}
