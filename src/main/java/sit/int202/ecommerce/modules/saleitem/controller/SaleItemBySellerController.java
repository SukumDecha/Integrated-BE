package sit.int202.ecommerce.modules.saleitem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sit.int202.ecommerce.common.dto.PaginateResponse;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemDetailResponse;
import sit.int202.ecommerce.modules.saleitem.service.SaleItemService;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemPaginationRequest;
import sit.int202.ecommerce.modules.user.model.UserAccountType;


import java.util.List;

@RestController
@RequestMapping("/v2/sellers")
@RequiredArgsConstructor
public class SaleItemBySellerController {

    private final SaleItemService saleItemService;

    @GetMapping("/{id}/sale-items")
    @Operation(summary = "View Sale Items by Seller ID (Seller only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of sale items owned by this seller"),
            @ApiResponse(responseCode = "400", description = "Missing/Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Not seller or ID mismatch")
    })
    public ResponseEntity<?> getSaleItemsBySeller(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute SaleItemPaginationRequest request,
            HttpServletRequest httpRequest
    ) {
        System.out.println("==== [TOKEN DEBUG] ====");
        System.out.println("Authorization: " + httpRequest.getHeader("Authorization"));
        System.out.println("UserPrincipal: " + user);
        System.out.println("Role: " + (user != null ? user.getRole() : "null"));
        System.out.println("ID: " + (user != null ? user.getId() : "null"));
        System.out.println("Path ID: " + id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        UserAccountType role = UserAccountType.valueOf(user.getRole());

        if (role != UserAccountType.SELLER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: You are not a seller");
        }

        if (!id.equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: ID mismatch");
        }

        PaginateResponse<SaleItemDetailResponse> saleItems = saleItemService.getBySellerId(id, request);
        return ResponseEntity.ok(saleItems);
    }
}