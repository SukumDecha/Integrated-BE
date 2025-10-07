package sit.int202.ecommerce.modules.saleitem.dto.response;

import lombok.Data;
import sit.int202.ecommerce.modules.user.dto.response.UserSummaryResponse;

@Data
public class SaleItemGalleryResponse {
    private Integer id;
    private String model;
    private String brandName;
    private Integer price;
    private Integer ramGb;
    private Integer storageGb;
    private String color;
    private UserSummaryResponse seller;
    private Integer quantity;
}
