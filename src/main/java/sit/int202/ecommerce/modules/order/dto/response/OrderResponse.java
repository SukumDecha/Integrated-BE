package sit.int202.ecommerce.modules.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import sit.int202.ecommerce.modules.order.model.OrderStatus;
import sit.int202.ecommerce.modules.user.dto.response.UserSummaryResponse;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {

    private Integer id;

    private Integer buyerId;
    private UserSummaryResponse buyer;

    private Integer sellerId;
    private UserSummaryResponse seller;

    private String orderDate;
    private String orderNote;
    private String shippingAddress;

    private OrderStatus orderStatus;
    private List<OrderItemResponse> orderItems;
}
