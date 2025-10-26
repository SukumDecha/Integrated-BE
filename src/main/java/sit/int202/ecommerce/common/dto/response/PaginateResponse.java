package sit.int202.ecommerce.common.dto.response;


import lombok.Data;
import java.util.List;

@Data
public class PaginateResponse<T> {

        private List<T> content;
        private boolean last;
        private boolean first;
        private int totalPages;
        private long totalElements;
        private int size;
        private String sort;
        private int page;
    }

