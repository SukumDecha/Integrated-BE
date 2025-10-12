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
            @ApiResponse(responseCode = "401", description = "Seller not found or invalid token"),
            @ApiResponse(responseCode = "403", description = "User is not active or request seller id not matched with id in access token")
    })
    public ResponseEntity<?> getSaleItemsBySeller(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute SaleItemPaginationRequest request
    ) {
        PaginateResponse<SaleItemDetailResponse> response = saleItemService.getBySellerId(id, request, user);
        return ResponseEntity.ok(response);
    }
}