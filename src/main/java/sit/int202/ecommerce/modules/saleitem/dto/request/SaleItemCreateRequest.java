package sit.int202.ecommerce.modules.saleitem.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import sit.int202.ecommerce.modules.brand.dto.request.BrandRequest;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleItemCreateRequest {

    @NotNull(message = "Brand cannot be null")
    private BrandRequest brand;

    @NotNull(message = "Category ID cannot be null")
    @NotBlank(message = "Model cannot be blank")
    private String model;

    @Min(value = 0, message = "Price must be at least 0")
    private Integer price;

    @Min(value = 1, message = "Discount must be at least 1")
    private Integer ramGb;

    @DecimalMin(value = "0.0", message = "Screen size must be at least 0")
    @DecimalMax(value = "99.99", message = "Screen size must not exceed 99.99")
    @Digits(integer = 2, fraction = 2, message = "Screen size must be a decimal with two decimal")
    private BigDecimal screenSizeInch;

    @Min(value = 1, message = "Storage must be at least 0")
    private Integer storageGb;

    private String color;

    private Integer quantity;

    private String description;

    private List<SaleItemImageRequest> imageInfos;

    public void normalize() {
        if (model != null) {
            model = model.trim();

            if (model.isBlank()) {
                model = null;
            }
        }
        if (description != null) {
            description = description.trim();

            if (description.isBlank()) {
                description = null;
            }
        }
        if (color != null) {
            color = color.trim();

            if (color.isBlank()) {
                color = null;
            }
        }
//        if (quantity == null || quantity < 1) {
//            quantity = 1;
//        }

    }
}

