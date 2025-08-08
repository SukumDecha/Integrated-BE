package sit.int202.ecommerce.modules.saleitem.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemCreateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemUpdateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemDetailResponse;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemGalleryResponse;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemListResponse;
import sit.int202.ecommerce.modules.saleitem.model.SaleItem;

@Component
public class SaleItemMapper {

    private final ModelMapper modelMapper;

    public SaleItemMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SaleItemDetailResponse toDetailResponse(SaleItem saleItem) {
        return modelMapper.map(saleItem, SaleItemDetailResponse.class);
    }

    public SaleItemGalleryResponse toGalleryResponse(SaleItem saleItem) {
        return modelMapper.map(saleItem, SaleItemGalleryResponse.class);
    }

    public SaleItemListResponse toListResponse(SaleItem saleItem) {
        return modelMapper.map(saleItem, SaleItemListResponse.class);
    }

    public SaleItem toEntity(SaleItemCreateRequest request) {
        return modelMapper.map(request, SaleItem.class);
    }

    public SaleItem toEntity(SaleItemUpdateRequest request, SaleItem existingSaleItem) {
        modelMapper.map(request, existingSaleItem);
        return existingSaleItem;
    }
}