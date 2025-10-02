package sit.int202.ecommerce.modules.order.dto.response;

import lombok.Data;
import sit.int202.ecommerce.modules.order.model.OrderStatus;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;

import java.util.List;

@Data
public class OrderResponse {

    private Integer id;
    private Integer buyerId;
    private UserResponse seller;

    private String orderDate;
    private String orderNote;
    private String shippingAddress;

    private OrderStatus orderStatus;
    private List<OrderItemResponse> orderItems;
}
