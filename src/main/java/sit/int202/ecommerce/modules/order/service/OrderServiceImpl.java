package sit.int202.ecommerce.modules.order.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.common.dto.PaginateResponse;
import sit.int202.ecommerce.common.dto.request.PaginationRequest;
import sit.int202.ecommerce.common.exceptions.BadRequestException;
import sit.int202.ecommerce.common.exceptions.ForbiddenException;
import sit.int202.ecommerce.common.exceptions.ResourceConflictException;
import sit.int202.ecommerce.common.utils.PaginationUtils;
import sit.int202.ecommerce.modules.order.dto.request.OrderRequest;
import sit.int202.ecommerce.modules.order.dto.response.OrderResponse;
import sit.int202.ecommerce.modules.order.mapper.OrderMapper;
import sit.int202.ecommerce.modules.order.model.Order;
import sit.int202.ecommerce.modules.order.model.OrderItem;
import sit.int202.ecommerce.modules.order.repository.OrderRepository;
import sit.int202.ecommerce.modules.saleitem.repository.SaleItemRepository;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;
import sit.int202.ecommerce.modules.user.model.UserAccount;
import sit.int202.ecommerce.modules.user.model.UserAccountType;
import sit.int202.ecommerce.modules.user.repository.UserAccountRepository;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserAccountRepository userAccountRepository;
    private final SaleItemRepository saleItemRepository;

    private final OrderMapper orderMapper;

    @Override
    public List<OrderResponse> placeOrder(List<OrderRequest> orderRequestList) {
        return orderRequestList.stream().map(orderRequest -> {
            Order order = orderMapper.toOrderEntity(orderRequest);

            UserAccount buyer = userAccountRepository.findById(orderRequest.getBuyerId())
                    .orElseThrow(() -> new BadRequestException("User not found with id: " + orderRequest.getBuyerId()));
            order.setBuyer(buyer);

            UserAccount seller = userAccountRepository.findById(orderRequest.getSellerId())
                    .orElseThrow(() -> new BadRequestException("User not found with id: " + orderRequest.getSellerId()));
            order.setSeller(seller);

            if (Objects.equals(buyer.getId(), seller.getId())) {
                throw new EntityNotFoundException("Buyer and Seller cannot be the same user.");
            }

            if (seller.getType() != UserAccountType.SELLER) {
                throw new EntityNotFoundException("The specified sellerId does not belong to a seller.");
            }

            List<OrderItem> orderItems = orderRequest.getOrderItems().stream()
                    .map(orderItemRequest -> {
                        OrderItem orderItem = orderMapper.toOrderItemEntity(order, orderItemRequest);

                        var saleItem = saleItemRepository.findById(orderItemRequest.getSaleItemId())
                                .orElseThrow(() -> new EntityNotFoundException("SaleItem not found with id: " + orderItemRequest.getSaleItemId()));

                        if (!Objects.equals(saleItem.getSeller().getId(), seller.getId())) {
                            throw new BadRequestException("SaleItem id: " + orderItemRequest.getSaleItemId() + " does not belong to Seller id: " + seller.getId());
                        }

                        if (saleItem.getQuantity() < orderItemRequest.getQuantity()) {
                            throw new ResourceConflictException("Insufficient stock for SaleItem id: " + orderItemRequest.getSaleItemId());
                        }

                        orderItem.setSaleItem(saleItem);
                        orderItem.setOrder(order);
                        orderItem.setBuyer(buyer);
                        return orderItem;
                    }).toList();

            order.setOrderItems(orderItems);
            orderRepository.saveAndFlush(order);

            return orderMapper.toOrderResponse(order, false);
        }).toList();
    }

    @Override
    public OrderResponse findOrderById(UserPrincipal currentUser, Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found with id: " + orderId));

        if (!Objects.equals(order.getBuyer().getId(), currentUser.getId()) &&
            !Objects.equals(order.getSeller().getId(), currentUser.getId())) {
            throw new ForbiddenException("Access denied: You are neither the buyer nor the seller of this order.");
        }

        return orderMapper.toOrderResponse(order, false);
    }

    @Override
    public PaginateResponse<OrderResponse> getOrdersByBuyerId(Integer buyerId, UserPrincipal currentUser, PaginationRequest pagination) {
        if (!buyerId.equals(currentUser.getId())) {
            throw new ForbiddenException("Access denied: Buyer ID mismatch.");
        }

        if (userAccountRepository.findById(buyerId).orElse(null) == null) {
            throw new EntityNotFoundException("User not found with id: " + buyerId);
        }

        Pageable pageable = PaginationUtils.buildPageable(pagination);
        Specification<Order> specification = (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("buyer").get("id"), buyerId)
                );

        Page<Order> orderPage = orderRepository.findAll(specification, pageable);
        Page<OrderResponse> orderResponsePage = orderPage.map(
                order -> orderMapper.toOrderResponse(order, false)
        );

        return PaginationUtils.toPaginateResponse(orderResponsePage);
    }

    @Override
    public PaginateResponse<OrderResponse> getOrdersBySellerId(Integer sellerId, UserPrincipal currentUser, PaginationRequest pagination) {
        if (!sellerId.equals(currentUser.getId())) {
            throw new ForbiddenException("Access denied: Seller ID mismatch.");
        }

        UserAccount userAccount = userAccountRepository.findById(sellerId)
                .orElseThrow(() -> new BadRequestException("User not found with id: " + sellerId));

        if (userAccount.getType() != UserAccountType.SELLER) {
            throw new ForbiddenException("Access denied: User is not a seller.");
        }

        Pageable pageable = PaginationUtils.buildPageable(pagination);
        Specification<Order> specification = (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("seller").get("id"), sellerId)
                );

        Page<Order> orderPage = orderRepository.findAll(specification, pageable);
        Page<OrderResponse> orderResponsePage = orderPage.map(
                order -> orderMapper.toOrderResponse(order, true)
        );

        return PaginationUtils.toPaginateResponse(orderResponsePage);
    }
}
