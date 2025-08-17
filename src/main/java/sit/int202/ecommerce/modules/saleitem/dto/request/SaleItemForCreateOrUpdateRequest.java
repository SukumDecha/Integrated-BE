package sit.int202.ecommerce.modules.saleitem.dto.request;

import lombok.Data;
import sit.int202.ecommerce.modules.brand.dto.request.BrandRequest;

@Data
public class SaleItemForCreateOrUpdateRequest {
    private String model;
    private Double price;
    private String description;
    private BrandRequest brand;
    private Integer ramGb;
    private Integer screenSizeInch;
    private Integer storageGb;
    private String color;
    private Integer quantity;
}