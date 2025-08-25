package sit.int202.ecommerce.modules.saleitem.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import sit.int202.ecommerce.modules.file.mapper.FileMapper;
import sit.int202.ecommerce.modules.file.model.FileEntity;
import sit.int202.ecommerce.modules.file.service.FileServiceImpl;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemCreateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemUpdateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemDetailResponse;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemGalleryResponse;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemListResponse;
import sit.int202.ecommerce.modules.saleitem.model.SaleItem;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SaleItemMapper {

    private final ModelMapper modelMapper;
    private final FileMapper fileMapper;
    private final FileServiceImpl fileService;

    public SaleItemDetailResponse toDetailResponse(SaleItem saleItem) {
        SaleItemDetailResponse dto = modelMapper.map(saleItem, SaleItemDetailResponse.class);

        if (saleItem.getBrand() != null) {
            dto.setBrandName(saleItem.getBrand().getName());
        }

        List<FileEntity> files = fileService.getFilesByReference("SALE_ITEM", saleItem.getId());
        if (!files.isEmpty()) {
            dto.setSaleItemImages(
                    files.stream()
                            .sorted(Comparator.comparing(f -> f.getDisplayOrder() != null ? f.getDisplayOrder() : 0))
                            .map(fileMapper::toResponse)
                            .toList()
            );
        }

        return dto;
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