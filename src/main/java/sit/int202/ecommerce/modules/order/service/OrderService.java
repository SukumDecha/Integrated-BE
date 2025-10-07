package sit.int202.ecommerce.modules.order.service;

import sit.int202.ecommerce.common.dto.PaginateResponse;
import sit.int202.ecommerce.common.dto.request.PaginationRequest;
import sit.int202.ecommerce.modules.order.dto.request.OrderRequest;
import sit.int202.ecommerce.modules.order.dto.response.OrderResponse;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;

import java.util.List;

public interface OrderService {

    List<OrderResponse> placeOrder(List<OrderRequest> orders);

    OrderResponse findOrderById(UserPrincipal currentUser, Integer orderId);

    PaginateResponse<OrderResponse> getOrdersByBuyerId(Integer buyerId, UserPrincipal currentUser, PaginationRequest pagination);
    PaginateResponse<OrderResponse> getOrdersBySellerId(Integer sellerId, UserPrincipal currentUser, PaginationRequest pagination);

}
