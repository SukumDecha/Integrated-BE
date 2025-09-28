package sit.int202.ecommerce.modules.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sit.int202.ecommerce.common.dto.PaginateResponse;
import sit.int202.ecommerce.common.dto.request.PaginationRequest;
import sit.int202.ecommerce.modules.order.dto.request.OrderRequest;
import sit.int202.ecommerce.modules.order.dto.response.OrderResponse;
import sit.int202.ecommerce.modules.order.service.OrderService;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;

@RestController()
@Tag(name = "Order", description = "Endpoints for managing orders")
@AllArgsConstructor()
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/v2/orders")
    @Operation(
            summary = "Place a new order",
            description = "Creates a new order based on the provided order details."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Order placed successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid order details provided"
                    )
            }
    )
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("/v2/orders/{id}")
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves order details by its ID."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Order found and returned successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Order not found with the provided ID"
                    )
            }
    )
    public OrderResponse findOrderById(@PathVariable Integer id) {
        return orderService.findOrderById(id);
    }

    @GetMapping("/v2/users/{buyerId}/orders")
    @Operation(
            summary = "Get orders by buyer ID",
            description = "Retrieves a list of orders placed by the specified buyer."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of orders retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginateResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Buyer not found with the provided ID"
                    )
            }
    )
    public ResponseEntity<PaginateResponse<OrderResponse>> getOrdersByBuyerId(@PathVariable Integer buyerId,
                                                                              @AuthenticationPrincipal UserPrincipal currentUser,
                                                                              @Valid @ModelAttribute PaginationRequest pagination) {
        return ResponseEntity.ok(orderService.getOrdersByBuyerId(buyerId, currentUser, pagination));
    }

    @GetMapping("/v2/sellers/{sellerId}/orders")
    @Operation(
            summary = "Get orders by seller ID",
            description = "Retrieves a list of orders received by the specified seller."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of orders retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginateResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Seller not found with the provided ID"
                    )
            }
    )
    public ResponseEntity<PaginateResponse<OrderResponse>> getOrdersBySellerId(@PathVariable Integer sellerId,
                                                                               @AuthenticationPrincipal UserPrincipal currentUser,
                                                                               @Valid @ModelAttribute PaginationRequest pagination) {
        return ResponseEntity.ok(orderService.getOrdersBySellerId(sellerId, currentUser, pagination));
    }
}
