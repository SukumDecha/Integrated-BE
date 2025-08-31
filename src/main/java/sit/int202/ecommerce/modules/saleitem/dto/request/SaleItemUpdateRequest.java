package sit.int202.ecommerce.modules.saleitem.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import sit.int202.ecommerce.modules.brand.dto.request.BrandRequest;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleItemUpdateRequest {

    private BrandRequest brand;

    private String model;

    @Min(value = 0, message = "Price must be at least 0")
    private Integer price;

    @Min(value = 1, message = "Discount must be at least 1")
    private Integer ramGb;

    @DecimalMin(value = "0.0", message = "Screen size must be at least 0")
    @DecimalMax(value = "99.99", message = "Screen size must not exceed 99.99")
    @Digits(integer = 2, fraction = 2, message = "Screen size must be a decimal with two decimal places")
    private BigDecimal screenSizeInch;

    @Min(value = 1, message = "Storage must be at least 1")
    private Integer storageGb;

    private String color;

    private Integer quantity;

    private String description;

    private List<SaleItemImageRequest> imageInfos;

    public void setModel(String model) {
        if (model != null) {
            model = model.trim();

            if (model.isBlank()) {
                model = null;
            }
        }

        this.model = model;
    }

    public void setDescription(String description) {
        if (description != null) {
            description = description.trim();

            if (description.isBlank()) {
                description = null;
            }
        }

        this.description = description;
    }

    public void setColor(String color) {
        if (color != null) {
            color = color.trim();

            if (color.isBlank()) {
                color = null;
            }
        }

        this.color = color;
    }
}