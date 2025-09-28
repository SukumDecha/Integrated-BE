package sit.int202.ecommerce.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PaginationRequest {

    @Schema(description = "Page number (0-based)", example = "0", defaultValue = "0")
    @Min(value = 0, message = "Page must be 0 or greater")
    private Integer page = 0;

    @Schema(description = "Number of items per page", example = "20", defaultValue = "20")
    @Min(value = 1, message = "Size must be at least 1")
    private Integer size = 20;

    @Schema(description = "Field name to sort by", example = "createdOn")
    private String sortBy = "id";

    @Schema(description = "Sort direction (ASC or DESC)", example = "DESC")
    private String sortDirection = "ASC";

}
