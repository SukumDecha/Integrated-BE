package sit.int202.ecommerce.dto;

import lombok.Data;

@Data
public class SaleItemGalleryResponse {
    private String model;
    private String brandName;
    private Integer price;
    private Integer storageGb;
    private String color;
}
