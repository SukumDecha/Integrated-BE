package sit.int202.ecommerce.modules.saleitem.dto.request;

import lombok.Data;
import sit.int202.ecommerce.common.dto.request.PaginationRequest;

import java.util.List;

@Data
public class SaleItemPaginationRequest extends PaginationRequest {

    private List<String> filterBrands;
    private List<Integer> filterStorages;
    private Integer filterPriceLower;
    private Integer filterPriceUpper;

    private String filterSearch;
}
