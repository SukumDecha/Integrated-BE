package sit.int202.ecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int202.ecommerce.dto.request.BrandUpdateRequest;
import sit.int202.ecommerce.dto.response.BrandResponse;
import sit.int202.ecommerce.dto.response.BrandUpdateResponse;
import sit.int202.ecommerce.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/v1/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    @Operation(
            summary = "Get all brands",
            description = "Retrieves a list of all brands available in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of brands retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public List<BrandResponse> getAllBrands() {
        return brandService.getAllBrands();
    }


    @PutMapping("/{id}")
    @Operation(
            summary = "Update brand by ID",
            description = "Updates the brand details by its ID. Handles empty or optional fields gracefully."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Brand updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandUpdateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Brand with given ID not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Duplicate brand name or invalid data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public BrandUpdateResponse updateBrand(@PathVariable Integer id, @RequestBody BrandUpdateRequest payload) {
        return brandService.updateBrand(id, payload);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete brand by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Brand deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing ID"),
            @ApiResponse(responseCode = "404", description = "Brand does not exist")
    })
    public ResponseEntity<Void> deleteBrand(@PathVariable(required = true) Integer id) {
        brandService.deleteBrandById(id);
        return ResponseEntity.noContent().build();
    }


}
