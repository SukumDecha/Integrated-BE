package sit.int202.ecommerce.modules.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {

    @NotNull(message = "SaleItemId cannot be null")
    private Integer saleItemId;

    @Min(value = 0, message = "Price must be at least 0")
    private Integer price;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String description;

    public void setDescription(String description) {
        if (description != null) {
            description = description.trim();

            if (description.isBlank()) {
                description = null;
            }
        }

        this.description = description;
    }

}
