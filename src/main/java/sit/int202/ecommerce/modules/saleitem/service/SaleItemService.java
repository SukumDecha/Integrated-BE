package sit.int202.ecommerce.modules.saleitem.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.common.dto.PaginateResponse;
import sit.int202.ecommerce.common.exceptions.FileUploadException;
import sit.int202.ecommerce.common.utils.PaginationUtils;
import sit.int202.ecommerce.common.utils.SortUtils;
import sit.int202.ecommerce.modules.brand.dto.response.BrandResponse;
import sit.int202.ecommerce.modules.brand.mapper.BrandMapper;
import sit.int202.ecommerce.modules.brand.model.Brand;
import sit.int202.ecommerce.modules.brand.service.BrandService;
import sit.int202.ecommerce.modules.file.model.File;
import sit.int202.ecommerce.modules.file.repository.FileRepository;
import sit.int202.ecommerce.modules.file.service.FileService;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemCreateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemImageRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemUpdateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemDetailResponse;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemGalleryResponse;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemListResponse;
import sit.int202.ecommerce.modules.saleitem.mapper.SaleItemMapper;
import sit.int202.ecommerce.modules.saleitem.model.SaleItem;
import sit.int202.ecommerce.modules.saleitem.repository.SaleItemRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleItemService {
    private final SaleItemRepository saleItemRepository;
    private final BrandService brandService;
    private final SaleItemMapper saleItemMapper;
    private final BrandMapper brandMapper;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final EntityManager em;

    public List<SaleItemGalleryResponse> getAllSaleItems(String sortBy, String sortDirection) {
        return saleItemRepository.findAll(SortUtils.buildSort(sortBy, sortDirection))
                .stream()
                .map(saleItemMapper::toGalleryResponse)
                .toList();
    }

    public List<SaleItemListResponse> getAllSaleItemList(String sortBy, String sortDirection) {
        return saleItemRepository.findAll(SortUtils.buildSort(sortBy, sortDirection))
                .stream()
                .map(saleItemMapper::toListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SaleItemDetailResponse getSaleItemById(Integer id) {
        SaleItem item = saleItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SaleItem not found for this id :: " + id));
        return saleItemMapper.toDetailResponse(item);
    }

    @Transactional
    public SaleItemDetailResponse createSaleItem(SaleItemCreateRequest item) {
        item.normalize();

        BrandResponse brandDTO = brandService.getBrandById(item.getBrand().getId());
        Brand brand = brandMapper.toEntity(brandDTO);

        SaleItem tempSaleItem = saleItemMapper.toEntity(item);
        tempSaleItem.setBrand(brand);
        SaleItem saleItem = saleItemRepository.save(tempSaleItem);

        log.info("Payload for create: ", item.toString());
        try {
            List<SaleItemImageRequest> imageInfos = item.getImageInfos();
            List<File> uploadedFiles = new ArrayList<>();

            if (imageInfos != null) {
                long newImageCount = imageInfos.stream()
                        .filter(info -> "NEW".equalsIgnoreCase(info.getStatus()))
                        .count();

                if (newImageCount > 4) {
                    throw new FileUploadException("You can upload up to 4 images only.");
                }

                for (SaleItemImageRequest info : imageInfos) {
                    if ("NEW".equalsIgnoreCase(info.getStatus()) && info.getImageFile() != null) {
                        if (info.getImageFile().getSize() > 2 * 1024 * 1024) {
                            throw new FileUploadException("Each image must be smaller than 2MB.");
                        }

                        File file = fileService.saveFile(
                                info.getImageFile(),
                                "SALE_ITEM",
                                saleItem.getId()
                        );

                        file.setDisplayOrder(info.getOrder() != null ? info.getOrder() : 0);
                        uploadedFiles.add(file);
                    }
                }

                if (!uploadedFiles.isEmpty()) {
                    fileRepository.saveAll(uploadedFiles);
                    em.refresh(saleItem);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image(s)", e);
        }

        em.refresh(saleItem);
        return saleItemMapper.toDetailResponse(saleItem);
    }

    @Transactional
    public SaleItemDetailResponse updateSaleItem(Integer id, SaleItemUpdateRequest item) {
        item.normalize();

        SaleItem existing = saleItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SaleItem not found with id :: " + id));

        BrandResponse brandDto = brandService.getBrandById(item.getBrand().getId());
        Brand brand = brandMapper.toEntity(brandDto);
        existing.setModel(item.getModel());
        existing.setPrice(item.getPrice());
        existing.setDescription(item.getDescription());
        existing.setBrand(brand);
        existing.setRamGb(item.getRamGb());
        existing.setScreenSizeInch(item.getScreenSizeInch());
        existing.setStorageGb(item.getStorageGb());
        existing.setColor(item.getColor());
        existing.setQuantity(item.getQuantity());

        List<SaleItemImageRequest> imageInfos = item.getImageInfos();
        List<File> existingFiles = fileService.getFilesByRef("SALE_ITEM", id);
        Set<String> keepFileNames = new HashSet<>();
        List<File> newFilesToStore = new ArrayList<>();

        log.info("Payload for create: ", item.getImageInfos().toString());
        if (imageInfos != null) {
            if (imageInfos.size() > 4) {
                throw new FileUploadException("Maximum 4 images are allowed.");
            }

            for (SaleItemImageRequest info : imageInfos) {
                switch (info.getStatus().toUpperCase()) {
                    case "OLD":
                        if (info.getFileName() != null) {
                            keepFileNames.add(info.getFileName());
                        }
                        break;
                    case "DELETED":
                        if (info.getFileName() != null) {
                            existingFiles.stream()
                                    .filter(f -> f.getOriginalFilename().equals(info.getFileName()))
                                    .findFirst()
                                    .ifPresent(f -> fileService.deleteFile(f.getId()));
                        }
                        break;
                    case "NEW":
                        if (info.getImageFile() != null) {
                            if (info.getImageFile().getSize() > 2 * 1024 * 1024) {
                                throw new FileUploadException("Each image must be smaller than 2MB.");
                            }
                            try {
                                File newFile = fileService.storeFile(
                                        "SALE_ITEM", id, info.getImageFile(), info.getOrder()
                                );
                                newFilesToStore.add(newFile);
                            } catch (IOException e) {
                                throw new FileUploadException("Failed to store new image: " + e.getMessage());
                            }
                        }
                        break;
                }
            }

            // อัปเดตลำดับรูปที่ยังคงอยู่
            for (File file : existingFiles) {
                if (keepFileNames.contains(file.getOriginalFilename())) {
                    imageInfos.stream()
                            .filter(i -> file.getOriginalFilename().equals(i.getFileName()))
                            .findFirst()
                            .ifPresent(i -> file.setDisplayOrder(i.getOrder()));
                }
            }

            List<File> updatedFileList = new ArrayList<>();
            updatedFileList.addAll(existingFiles.stream()
                    .filter(f -> keepFileNames.contains(f.getOriginalFilename()))
                    .toList());
            updatedFileList.addAll(newFilesToStore);

            existing.setFiles(updatedFileList.stream()
                    .sorted(Comparator.comparing(File::getDisplayOrder))
                    .collect(Collectors.toList()));
        }

        return saleItemMapper.toDetailResponse(existing);
    }

    @Transactional
    public void deleteSaleItemById(Integer id) {
        if (!saleItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Sale item with ID " + id + " not found");
        }

        List<File> files = fileService.getFilesByRef("SALE_ITEM", id);

        for (File file : files) {
            fileService.deleteFile(file.getId());
        }

        saleItemRepository.deleteById(id);
    }

    public PaginateResponse<SaleItemDetailResponse> getSaleItems(
            int page,
            int size,
            String sortField,
            String sortDirection,
            List<String> filterBrands,
            List<Integer> filterStorages,
            Integer filterPriceLower,
            Integer filterPriceUpper
    ) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (sortField != null && !sortField.isBlank()) {
            sorts.add(new Sort.Order(Sort.Direction.fromString(sortDirection), sortField));
        }

        sorts.add(Sort.Order.asc("createdOn"));
        sorts.add(Sort.Order.asc("id"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sorts));

        Page<SaleItem> saleItems;
        if (filterBrands != null && !filterBrands.isEmpty()) {
            saleItems = saleItemRepository.findByBrand_NameIn(filterBrands, pageable);
        } else {
            saleItems = saleItemRepository.findAll(pageable);
        }

        Page<SaleItemDetailResponse> dtoPage = saleItems.map(saleItemMapper::toDetailResponse);
        return PaginationUtils.toPaginateResponse(dtoPage);
    }
}