package sit.int202.ecommerce.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.int202.ecommerce.dto.response.SaleItemGalleryResponse;
import sit.int202.ecommerce.dto.response.SaleItemDetailResponse;
import sit.int202.ecommerce.service.SaleItemService;

import java.util.List;

@RestController
@RequestMapping("/itb-mshop/v1/sale-items")
public class SaleItemController {

    @Autowired
    private SaleItemService saleItemService;

    @GetMapping ("/{id}")
    @Operation(
            summary = "Get sale item by ID",
            description = "Returns sale item details by its ID. Handles empty or optional fields gracefully."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sale item found and returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleItemDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sale item with given ID not found",
                    content = @Content
            )
    })
    public ResponseEntity<SaleItemDetailResponse> getSaleItemDetail(@Parameter(description = "ID of the sale item to be retrieved", required = true) @PathVariable Integer id) {
        var saleItem = saleItemService.getSaleItemById(id);
        return ResponseEntity.ok(saleItem);
    }

    @GetMapping
    @Operation(
            summary = "Get all sale items",
            description = "Returns a list of sale items (maximum 60 entries). Returns an empty array if no items exist."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of sale items (can be empty)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleItemGalleryResponse.class))
            )
    })
    public ResponseEntity<List<SaleItemGalleryResponse>> getAllSaleItems() {
        var saleItems = saleItemService.getAllSaleItems();
        return ResponseEntity.ok(saleItems);
    }


}
