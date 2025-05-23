package sit.int202.ecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sit.int202.ecommerce.dto.response.SaleItemDetailResponse;
import sit.int202.ecommerce.dto.response.SaleItemPaginateResponse;
import sit.int202.ecommerce.service.SaleItemService;

import java.util.List;

@RestController
@RequestMapping("/v2/sale-items")
@RequiredArgsConstructor
public class SaleItemV2Controller {
    private final SaleItemService saleItemService;

    @Operation(summary = "Get all sale items with pagination, brand filter, sorting")
    @GetMapping
    public SaleItemPaginateResponse<SaleItemDetailResponse> getSaleItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) List<String> filterBrands
    ) {
        return saleItemService.getSaleItems(page, size, sortField, sortDirection, filterBrands);
    }
}
