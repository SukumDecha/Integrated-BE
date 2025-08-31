package sit.int202.ecommerce.common.dto.request;

import lombok.Data;

@Data
public class PaginationRequest {

    private Integer page;
    private Integer size;

    private String sortBy;
    private String sortDirection;
}
