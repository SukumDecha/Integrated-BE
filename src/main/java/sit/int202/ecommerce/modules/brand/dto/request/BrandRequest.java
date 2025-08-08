package sit.int202.ecommerce.modules.brand.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BrandRequest {
    @NotNull(message = "Brand ID cannot be null")
    private Integer id;
    @NotNull(message = "Brand name cannot be null")
    private String name;
}
