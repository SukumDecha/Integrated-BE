package sit.int202.ecommerce.modules.order.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sit.int202.ecommerce.modules.order.model.OrderStatus;

import java.time.Instant;
import java.util.List;

@Data
public class OrderRequest {

    @NotNull(message = "BuyerId cannot be null")
    private Integer buyerId;

    @NotNull(message = "SellerId cannot be null")
    private Integer sellerId;

    @NotNull(message = "ShippingAddress cannot be null")
    @NotBlank(message = "ShippingAddress cannot be blank")
    private String shippingAddress;

    private String orderNote;

    @NotNull(message = "OrderDate cannot be null")
//    Format as ISO Date
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant orderDate;

    @NotNull(message = "OrderStatus cannot be null")
    private OrderStatus orderStatus;

    @NotNull(message = "OrderItems cannot be null")
    private List<OrderItemRequest> orderItems;

    public void setOrderNote(String orderNote) {
        if (orderNote != null) {
            orderNote = orderNote.trim();

            if (orderNote.isBlank()) {
                orderNote = null;
            }
        }

        this.orderNote = orderNote;
    }

    public void setShippingAddress(String shippingAddress) {
        if (shippingAddress != null) {
            shippingAddress = shippingAddress.trim();

            if (shippingAddress.isBlank()) {
                shippingAddress = null;
            }
        }

        this.shippingAddress = shippingAddress;
    }
}
