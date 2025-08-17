package sit.int202.ecommerce.modules.saleitem.dto.response;

import lombok.Data;
import sit.int202.ecommerce.modules.file.dto.FileResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class SaleItemDetailResponse extends SaleItemGalleryResponse {
    private String description;
    private BigDecimal screenSizeInch;
    private Integer quantity;

    private Instant createdOn;
    private Instant updatedOn;

    private List<FileResponse> saleItemImages;
}

