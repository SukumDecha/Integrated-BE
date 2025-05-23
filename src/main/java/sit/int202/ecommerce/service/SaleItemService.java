package sit.int202.ecommerce.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.dto.request.SaleItemCreateRequest;
import sit.int202.ecommerce.dto.request.SaleItemUpdateRequest;
import sit.int202.ecommerce.dto.response.*;
import sit.int202.ecommerce.model.Brand;
import sit.int202.ecommerce.model.SaleItem;
import sit.int202.ecommerce.repository.SaleItemRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SaleItemService {
    private final SaleItemRepository saleItemRepository;
    private final BrandService brandService;
    private final ModelMapper mapper;

    private final EntityManager em;

    public List<SaleItemGalleryResponse> getAllSaleItems() {
        return saleItemRepository.findAllByOrderByCreatedOnAscIdAsc().stream()
                .map(item -> mapper.map(item, SaleItemGalleryResponse.class))
                .collect(Collectors.toList());
    }

    public List<SaleItemListResponse> getAllSaleItemList() {
        return saleItemRepository.findAllByOrderByCreatedOnAscIdAsc().stream()
                .map(item -> mapper.map(item, SaleItemListResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public SaleItemDetailResponse getSaleItemById(Integer id) {
        SaleItem item = saleItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("SaleItem not found for this id :: " + id)
        );

        return mapper.map(item, SaleItemDetailResponse.class);
    }


    @Transactional
    public SaleItemDetailResponse createSaleItem(SaleItemCreateRequest item) {
        item.normalize();

        BrandResponse brandDTO = brandService.getBrandById(item.getBrand().getId());
        Brand brand = mapper.map(brandDTO, Brand.class);

        SaleItem reqSaleItem = mapper.map(item, SaleItem.class);
        reqSaleItem.setBrand(brand);

        SaleItem newSaleItem = saleItemRepository.save(reqSaleItem);

        em.refresh(newSaleItem);
        return mapper.map(newSaleItem, SaleItemDetailResponse.class);
    }

    @Transactional
    public SaleItemDetailResponse updateSaleItem(Integer id, SaleItemUpdateRequest item) {
        item.normalize();

        SaleItem existingSaleItem = saleItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SaleItem not found with id :: " + id));

        // Don't touch the existing brand - important!
        BrandResponse brandDTO = brandService.getBrandById(item.getBrand().getId());
        Brand brand = mapper.map(brandDTO, Brand.class);

        // Update fields
        existingSaleItem.setModel(item.getModel());
        existingSaleItem.setPrice(item.getPrice());
        existingSaleItem.setDescription(item.getDescription());
        existingSaleItem.setBrand(brand);
        existingSaleItem.setRamGb(item.getRamGb());
        existingSaleItem.setScreenSizeInch(item.getScreenSizeInch());
        existingSaleItem.setStorageGb(item.getStorageGb());
        existingSaleItem.setColor(item.getColor());
        existingSaleItem.setQuantity(item.getQuantity());

        SaleItem updatedSaleItem = saleItemRepository.save(existingSaleItem);
//        em.refresh(updatedSaleItem);

        return mapper.map(updatedSaleItem, SaleItemDetailResponse.class);
    }

    public void deleteSaleItemById(Integer id) {
        if (!saleItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Sale item with ID " + id + " not found");
        }
        saleItemRepository.deleteById(id);
    }

    public SaleItemPaginateResponse<SaleItemDetailResponse> getSaleItems(
            int page,
            int size,
            String sortField,
            String sortDirection,
            List<String> filterBrands
    ) {
        Pageable pageable = createPageable(page, size, sortField, sortDirection);
        Page<SaleItem> saleItems = findSaleItemsByBrands(filterBrands, pageable);
        return toPaginateResponse(saleItems);
    }

    private Pageable createPageable(int page, int size, String sortField, String sortDirection) {
        if (sortField == null || sortField.isBlank()) {
            return PageRequest.of(page, size, Sort.by("id"));
        }
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

    private Page<SaleItem> findSaleItemsByBrands(List<String> filterBrands, Pageable pageable) {
        if (filterBrands != null && !filterBrands.isEmpty()) {
            return saleItemRepository.findByBrand_NameIn(filterBrands, pageable);
        }
        return saleItemRepository.findAll(pageable);
    }

    private SaleItemPaginateResponse<SaleItemDetailResponse> toPaginateResponse(Page<SaleItem> saleItems) {
        Page<SaleItemDetailResponse> dtoPage = saleItems.map(item -> mapper.map(item, SaleItemDetailResponse.class));

        SaleItemPaginateResponse<SaleItemDetailResponse> response = new SaleItemPaginateResponse<>();
        response.setContent(dtoPage.getContent());
        response.setFirst(dtoPage.isFirst());
        response.setLast(dtoPage.isLast());
        response.setPage(dtoPage.getNumber());
        response.setSize(dtoPage.getSize());
        response.setTotalPages(dtoPage.getTotalPages());
        response.setTotalElements(dtoPage.getTotalElements());
        response.setSort(dtoPage.getSort().toString());

        return response;
    }





}

