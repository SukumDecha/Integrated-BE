package sit.int202.ecommerce.modules.saleitem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.ecommerce.common.dto.PaginateResponse;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemCreateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemPaginationRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemUpdateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemDetailResponse;
import sit.int202.ecommerce.modules.saleitem.service.SaleItemService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v2/sale-items")
@RequiredArgsConstructor
public class SaleItemV2Controller {
    private final SaleItemService saleItemService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Get all sale items with pagination, brand filter, sorting")
    @GetMapping
    public PaginateResponse<SaleItemDetailResponse> getSaleItems(SaleItemPaginationRequest request) {
        return saleItemService.getSaleItems(request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create sale item with image files (multipart/form-data)")
    @ApiResponse(responseCode = "201", description = "Sale item created")
    @ApiResponse(responseCode = "500", description = "Sale item create failed")
    public ResponseEntity<SaleItemDetailResponse> createSaleItemMultipart(
            @RequestPart("saleItem") String saleItemJson,
            @RequestPart(value = "imageInfos", required = false) List<MultipartFile> imageFiles
    ) {
        try {
            // 🔍 LOG JSON ที่ถูกส่งมา
            System.out.println("[DEBUG] Raw JSON from client: " + saleItemJson);

            SaleItemCreateRequest saleItem = objectMapper.readValue(saleItemJson, SaleItemCreateRequest.class);

            System.out.println("[DEBUG] Parsed imageInfos: " + saleItem.getImageInfos());

            if (saleItem.getImageInfos() != null && imageFiles != null) {
                for (int i = 0; i < imageFiles.size(); i++) {
                    if (i < saleItem.getImageInfos().size()) {
                        saleItem.getImageInfos().get(i).setImageFile(imageFiles.get(i));
                        System.out.println("[DEBUG] Attached image: " + imageFiles.get(i).getOriginalFilename());
                    }
                }
            }

            var created = saleItemService.createSaleItem(saleItem);
            return ResponseEntity.status(201).body(created);

        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Sale item create failed", e);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update sale item with image files (multipart/form-data)")
    @ApiResponse(responseCode = "200", description = "Sale item updated")
    @ApiResponse(responseCode = "500", description = "Sale item update failed")
    public ResponseEntity<SaleItemDetailResponse> updateSaleItemMultipart(
            @PathVariable Integer id,
            @RequestPart("saleItem") String saleItemJson,
            @RequestPart(value = "imageInfos", required = false) List<MultipartFile> imageFiles
    ) {
        try {
            SaleItemUpdateRequest saleItem = objectMapper.readValue(saleItemJson, SaleItemUpdateRequest.class);

            if (saleItem.getImageInfos() != null && imageFiles != null) {
                for (int i = 0; i < imageFiles.size(); i++) {
                    if (i < saleItem.getImageInfos().size()) {
                        saleItem.getImageInfos().get(i).setImageFile(imageFiles.get(i));
                    }
                }
            }

            var updated = saleItemService.updateSaleItem(id, saleItem);
            return ResponseEntity.ok(updated);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Sale item update failed", e);
        }
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