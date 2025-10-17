package sit.int202.ecommerce.common.utils;

import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public final class SortUtils {

    // Tie-breakers that are always appended
    private static final Sort DEFAULT_TIE_BREAKERS =
            Sort.by("createdOn").ascending().and(Sort.by("id").ascending());

    private SortUtils() {
        // prevent instantiation
    }

    public static Sort buildSort(String sortBy, String direction) {
        if (!StringUtils.hasText(sortBy)) {
            return DEFAULT_TIE_BREAKERS;        // or Sort.unsorted() if you prefer
        }

        Sort.Direction dir = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(dir, sortBy).and(DEFAULT_TIE_BREAKERS);
    }
}