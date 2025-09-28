package sit.int202.ecommerce.common.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import sit.int202.ecommerce.common.dto.PaginateResponse;
import sit.int202.ecommerce.common.dto.request.PaginationRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemPaginationRequest;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtils {

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

    public static Pageable buildPageable(PaginationRequest request) {
        if (request == null) {
            request = new PaginationRequest(); // fallback if request is null
        }

        List<Sort.Order> orders = new ArrayList<>();

        if (request.getSortBy() != null && !request.getSortBy().isBlank()) {
            orders.add(new Sort.Order(
                    Sort.Direction.fromString(request.getSortDirection()),
                    request.getSortBy()
            ));
        }

        orders.add(Sort.Order.asc("createdOn"));
        orders.add(Sort.Order.asc("id"));

        return PageRequest.of(request.getPage(), request.getSize(), Sort.by(orders));
    }

}
