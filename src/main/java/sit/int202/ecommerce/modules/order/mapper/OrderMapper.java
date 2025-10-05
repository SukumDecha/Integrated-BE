package sit.int202.ecommerce.modules.order.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import sit.int202.ecommerce.modules.order.dto.request.OrderItemRequest;
import sit.int202.ecommerce.modules.order.dto.request.OrderRequest;
import sit.int202.ecommerce.modules.order.dto.response.OrderItemResponse;
import sit.int202.ecommerce.modules.order.dto.response.OrderResponse;
import sit.int202.ecommerce.modules.order.model.Order;
import sit.int202.ecommerce.modules.order.model.OrderItem;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;
import sit.int202.ecommerce.modules.user.mapper.UserMapper;

import java.util.Comparator;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ModelMapper modelMapper;
    private final UserMapper userMapper;


    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        OrderItemResponse dto = modelMapper.map(orderItem, OrderItemResponse.class);
        dto.setSaleItemId(orderItem.getSaleItem().getId());

        return dto;
    }

    public OrderResponse toOrderResponse(Order order, boolean includeBuyer) {
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);

        if (includeBuyer) {
            UserResponse buyer = userMapper.toUserResponse(order.getBuyer());
            orderResponse.setBuyer(buyer);

            orderResponse.setSellerId(order.getSeller().getId());
        } else {
            UserResponse seller = userMapper.toUserResponse(order.getSeller());
            orderResponse.setSeller(seller);

            orderResponse.setBuyerId(order.getBuyer().getId());
        }

        if (order.getOrderDate() != null) {
            orderResponse.setOrderDate(order.getOrderDate().toString());
        }

        var orderItems = order.getOrderItems().stream()
                .sorted(Comparator.comparingInt(i -> i.getSaleItem().getId()))
                .map(this::toOrderItemResponse)
                .toList();

        orderResponse.setOrderItems(orderItems);

        return orderResponse;
    }

    public Order toOrderEntity(OrderRequest request) {
        Order order = new Order();
        order.setShippingAddress(request.getShippingAddress());
        order.setOrderNote(request.getOrderNote());
        order.setOrderDate(request.getOrderDate().atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        order.setStatus(request.getOrderStatus());

        return order;
    }

    public OrderItem toOrderItemEntity(Order order, OrderItemRequest orderItemRequest) {
        OrderItem orderItem = modelMapper.map(orderItemRequest, OrderItem.class);

        orderItem.setOrder(order);
        return orderItem;
    }
}