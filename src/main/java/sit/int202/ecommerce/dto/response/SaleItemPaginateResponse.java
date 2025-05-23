package sit.int202.ecommerce.dto.response;


import lombok.Data;
import java.util.List;

@Data
public class SaleItemPaginateResponse<T> {

        private List<T> content;
        private boolean last;
        private boolean first;
        private int totalPages;
        private long totalElements;
        private int size;
        private String sort;
        private int page;
    }

