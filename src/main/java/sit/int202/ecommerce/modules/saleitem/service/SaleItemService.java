package sit.int202.ecommerce.modules.saleitem.service;

import org.springframework.transaction.annotation.Transactional;
import sit.int202.ecommerce.common.dto.response.PaginateResponse;
import sit.int202.ecommerce.modules.saleitem.dto.request.*;
import sit.int202.ecommerce.modules.saleitem.dto.response.*;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;

import java.util.List;

public interface SaleItemService {

    List<SaleItemGalleryResponse> getAllSaleItems(String sortBy, String sortDirection);

    List<SaleItemListResponse> getAllSaleItemList(String sortBy, String sortDirection);

    @Transactional
    SaleItemDetailResponse getSaleItemById(Integer id);

    @Transactional
    SaleItemDetailResponse createSaleItem(SaleItemCreateRequest item, Integer sellerId);

    @Transactional
    SaleItemDetailResponse updateSaleItem(Integer id, SaleItemUpdateRequest item);

    @Transactional
    void deleteSaleItemById(Integer id);

    @Transactional
    PaginateResponse<SaleItemDetailResponse> getSaleItems(SaleItemPaginationRequest request);

    List<Integer> getDistinctStorageSizes(Boolean includeNotSpecified);

    @Transactional
    PaginateResponse<SaleItemDetailResponse> getBySellerId(
            Integer sellerId,
            SaleItemPaginationRequest request,
            UserPrincipal currentUser
    );
}
