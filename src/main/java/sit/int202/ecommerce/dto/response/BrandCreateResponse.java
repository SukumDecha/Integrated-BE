
package sit.int202.ecommerce.dto.response;

import lombok.Data;

@Data
public class BrandCreateResponse {
    private Integer id;
    private String name;
    private String websiteUrl;
    private String countryOfOrigin;
    private Boolean isActive;
}

