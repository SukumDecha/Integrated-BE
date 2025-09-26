package sit.int202.ecommerce.modules.saleitem.dto.response;

import lombok.Data;
import sit.int202.ecommerce.modules.file.dto.FileResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class SaleItemDetailResponse {
    private Long id;
    private String model;
    private String brandName;
    private String description;
    private BigDecimal price;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer quantity;
    private Integer storageGb;
    private String color;
    private SellerSummaryResponse seller;

    private List<FileResponse> saleItemImages;
    private Instant createdOn;
    private Instant updatedOn;
}

