package sit.int202.ecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int202.ecommerce.dto.request.BrandCreateRequest;
import sit.int202.ecommerce.dto.request.BrandUpdateRequest;
import sit.int202.ecommerce.dto.response.BrandDetailResponse;
import sit.int202.ecommerce.dto.response.BrandResponse;
import sit.int202.ecommerce.dto.response.MyErrorResponse;
import sit.int202.ecommerce.exception.BrandHasSaleItemsException;
import sit.int202.ecommerce.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/v1/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    @Operation(summary = "Get all brands")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of brands retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        List<BrandResponse> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get brand by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found",
                    content = @Content(schema = @Schema(implementation = MyErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = MyErrorResponse.class)))
    })
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Integer id) {
        BrandResponse brand = brandService.getBrandById(id);
        return ResponseEntity.ok(brand);
    }

    @PostMapping
    @Operation(summary = "Create a new brand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Brand created successfully",
                    content = @Content(schema = @Schema(implementation = BrandDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Duplicate name",
                    content = @Content(schema = @Schema(implementation = MyErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = MyErrorResponse.class)))
    })
    public ResponseEntity<BrandDetailResponse> createBrand(@Valid @RequestBody BrandCreateRequest request) {
        BrandDetailResponse response = brandService.createBrand(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update brand by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand updated successfully",
                    content = @Content(schema = @Schema(implementation = BrandDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found",
                    content = @Content(schema = @Schema(implementation = MyErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Duplicate brand name or invalid data",
                    content = @Content(schema = @Schema(implementation = MyErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = MyErrorResponse.class)))
    })
    public ResponseEntity<BrandDetailResponse> updateBrand(@PathVariable Integer id, @RequestBody BrandUpdateRequest payload) {
        BrandDetailResponse updatedBrand = brandService.updateBrand(id, payload);
        return ResponseEntity.ok(updatedBrand);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete brand by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Brand deleted"),
            @ApiResponse(responseCode = "400", description = "Brand cannot be deleted",
                    content = @Content(schema = @Schema(implementation = MyErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found",
                    content = @Content(schema = @Schema(implementation = MyErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrandById(id); // ❗ จะ throw Exception ถ้ามีปัญหา
        return ResponseEntity.noContent().build();
    }

    // ✅ Handle brand not found (404)
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public MyErrorResponse handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return MyErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .errorMessage("Brand not found")
                .path(request.getRequestURI())
                .build();
    }

    // ✅ Handle brand with existing sale items (400)
    @ExceptionHandler(BrandHasSaleItemsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MyErrorResponse handleBrandHasSaleItems(BrandHasSaleItemsException ex, HttpServletRequest request) {
        return MyErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorMessage("Cannot delete brand because there are associated sale items")
                .path(request.getRequestURI())
                .build();
    }
}
