package sit.int202.ecommerce.util;

import org.springframework.data.domain.Page;
import sit.int202.ecommerce.dto.response.PaginateResponse;

public class PaginationUtil {

    public static <T> PaginateResponse<T> toPaginateResponse(Page<T> page) {
        PaginateResponse<T> response = new PaginateResponse<>();
        response.setContent(page.getContent());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setSort(page.getSort().toString());
        return response;
    }

}
