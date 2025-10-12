package sit.int202.ecommerce.modules.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
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

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<List<OrderResponse>> placeOrder(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody List<OrderRequest> orders) {
        List<OrderResponse> orderListResponse = orderService.placeOrder(currentUser, orders);
        return ResponseEntity.created(URI.create("/v2/orders")).body(orderListResponse);
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
    public OrderResponse findOrderById(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Integer id) {
        return orderService.findOrderById(currentUser, id);
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
                                                                               @Valid @ModelAttribute PaginationRequest pagination,
                                                                               @RequestParam(defaultValue = "new") String tab) {
        return ResponseEntity.ok(orderService.getOrdersBySellerId(sellerId, currentUser, pagination, tab));
    }
}
