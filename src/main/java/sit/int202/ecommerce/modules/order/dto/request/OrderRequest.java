package sit.int202.ecommerce.modules.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sit.int202.ecommerce.modules.order.model.OrderStatus;

import java.time.OffsetDateTime;
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
    private OffsetDateTime orderDate;

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

    public void setOrderStatus(OrderStatus orderStatus) {
        if (orderStatus == null) {
            orderStatus = OrderStatus.COMPLETED;
        }

        this.orderStatus = orderStatus;
    }
}
