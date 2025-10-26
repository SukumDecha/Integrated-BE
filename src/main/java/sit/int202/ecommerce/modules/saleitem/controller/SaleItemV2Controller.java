package sit.int202.ecommerce.modules.saleitem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.ecommerce.common.dto.PaginateResponse;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemCreateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemPaginationRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemUpdateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemDetailResponse;
import sit.int202.ecommerce.modules.saleitem.service.SaleItemServiceImpl;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/v2/sale-items")
@RequiredArgsConstructor
public class SaleItemV2Controller {
    private final SaleItemServiceImpl saleItemService;

    @Operation(summary = "Get all sale items with pagination, brand filter, sorting")
    @GetMapping
    public PaginateResponse<SaleItemDetailResponse> getSaleItems(SaleItemPaginationRequest request) {
        return saleItemService.getSaleItems(request);
    }

    @PostMapping()
    @Operation(summary = "Create sale item with image files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sale item created"),
            @ApiResponse(responseCode = "400", description = "Missing/Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Seller not found or invalid token"),
            @ApiResponse(responseCode = "403", description = "User is not active,request seller id not matched with id in access token"),
            @ApiResponse(responseCode = "404", description = "Brand not found")
    })
    public ResponseEntity<SaleItemDetailResponse> createSaleItem(
            @Valid @ModelAttribute SaleItemCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userDetails) {

        Integer sellerId = userDetails.getId();
        // userId จาก token แล้วส่งเข้าให้ service
        SaleItemDetailResponse response = saleItemService.createSaleItem(request, sellerId);

        // ส่งสถานะ 201 Created ตาม RESTful
        return ResponseEntity.status(201).body(response);

    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update sale item with image files")
    @ApiResponse(responseCode = "200", description = "Sale item updated")
    @ApiResponse(responseCode = "500", description = "Sale item update failed")
    public ResponseEntity<SaleItemDetailResponse> updateSaleItem(
            @Parameter(description = "ID of the sale item to be updated", required = true) @PathVariable Integer id,
            @ModelAttribute @Valid SaleItemUpdateRequest request) {
        var saleItem = saleItemService.updateSaleItem(id, request);
        return ResponseEntity.ok(saleItem);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sale item by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sale item deleted"),
            @ApiResponse(responseCode = "404", description = "Sale item does not exist")
    })
    public ResponseEntity<Void> deleteSaleItem(@PathVariable Integer id) {
        saleItemService.deleteSaleItemById(id);
        return ResponseEntity.noContent().build(); // 204
    }

    @Operation(summary = "Get distinct storage sizes for filtering (include -1 for 'Not specified')")
    @GetMapping("/storage-sizes")
    public List<Integer> getStorageSizes(
            @RequestParam(defaultValue = "true") boolean includeNotSpecified
    ) {
        return saleItemService.getDistinctStorageSizes(includeNotSpecified);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale item by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sale item found"),
            @ApiResponse(responseCode = "404", description = "Sale item not found")
    })
    public ResponseEntity<SaleItemDetailResponse> getSaleItemById(@PathVariable Integer id) {
        try {
            SaleItemDetailResponse item = saleItemService.getSaleItemById(id);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sale item not found");
        }
    }
}