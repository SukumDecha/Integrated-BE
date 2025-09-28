package sit.int202.ecommerce.modules.order.dto.response;

import lombok.Data;

@Data
public class OrderItemResponse {

    private Integer no;
    private Integer saleItemId;
    private Integer price;
    private Integer quantity;
    private String description;

}
