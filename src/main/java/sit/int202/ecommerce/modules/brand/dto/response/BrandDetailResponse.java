package sit.int202.ecommerce.modules.brand.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sit.int202.ecommerce.modules.saleitem.model.SaleItem;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class BrandDetailResponse extends BrandResponse {

    private String websiteUrl;
    private String countryOfOrigin;
    private Boolean isActive;

    @JsonIgnore
    private Set<SaleItem> saleItems;

    public Integer getNoOfSaleItems() {
        if (saleItems == null) {
            return 0;
        }
        return saleItems.size();
    }
}
