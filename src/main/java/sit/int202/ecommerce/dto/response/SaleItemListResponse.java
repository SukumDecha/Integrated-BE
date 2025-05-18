package sit.int202.ecommerce.dto.response;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class SaleItemListResponse {

    private Integer id;

    @NotBlank(message = "Brand name must not be blank")
    private String brandName;

    @NotBlank(message = "Model must not be blank")
    @Size(max = 60, message = "Model must not exceed 60 characters")
    private String model;

    @Min(value = 1, message = "RAM must be at least 1 GB")
    private Integer ramGb;

    @Min(value = 1, message = "Storage must be at least 1 GB")
    private Integer storageGb;

    @NotBlank(message = "Color must not be blank")
    private String color;

    @Min(value = 0, message = "Price must be at least 0")
    private Integer price;

}