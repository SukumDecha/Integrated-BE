package sit.int202.ecommerce.modules.saleitem.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.common.dto.PaginateResponse;
import sit.int202.ecommerce.common.exceptions.FileUploadException;
import sit.int202.ecommerce.common.utils.PaginationUtils;
import sit.int202.ecommerce.common.utils.SortUtils;
import sit.int202.ecommerce.modules.brand.dto.response.BrandResponse;
import sit.int202.ecommerce.modules.brand.mapper.BrandMapper;
import sit.int202.ecommerce.modules.brand.model.Brand;
import sit.int202.ecommerce.modules.brand.service.BrandService;
import sit.int202.ecommerce.modules.file.model.FileEntity;
import sit.int202.ecommerce.modules.file.repository.FileRepository;
import sit.int202.ecommerce.modules.file.service.FileServiceImpl;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemCreateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemImageRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemPaginationRequest;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemUpdateRequest;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemDetailResponse;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemGalleryResponse;
import sit.int202.ecommerce.modules.saleitem.dto.response.SaleItemListResponse;
import sit.int202.ecommerce.modules.saleitem.mapper.SaleItemMapper;
import sit.int202.ecommerce.modules.saleitem.model.SaleItem;
import sit.int202.ecommerce.modules.saleitem.repository.SaleItemRepository;

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
    private final FileServiceImpl fileService;
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
        BrandResponse brandDTO = brandService.getBrandById(item.getBrand().getId());
        Brand brand = brandMapper.toEntity(brandDTO);

        SaleItem tempSaleItem = saleItemMapper.toEntity(item);
        tempSaleItem.setBrand(brand);

        SaleItem saleItem = saleItemRepository.save(tempSaleItem);

        List<SaleItemImageRequest> imageInfos = item.getImageInfos();
        List<FileEntity> uploadedFiles = new ArrayList<>();

        if (imageInfos != null) {
            long newImageCount = imageInfos.size();

            if (newImageCount > 4) {
                throw new FileUploadException("You can upload up to 4 images only.");
            }

            for (SaleItemImageRequest info : imageInfos) {
                if (info.getImageFile() != null) {
                    if (info.getImageFile().getSize() > 2 * 1024 * 1024) {
                        throw new FileUploadException("Each image must be smaller than 2MB.");
                    }

                    FileEntity file = fileService.uploadSingleFile(
                            info.getImageFile(),
                            "SALE_ITEM",
                            saleItem.getId(),
                            info.getOrder() != null ? info.getOrder() : 0
                    );

                    file.setDisplayOrder(info.getOrder() != null ? info.getOrder() : 0);
                    uploadedFiles.add(file);
                }
            }

            if (!uploadedFiles.isEmpty()) {
                fileRepository.saveAll(uploadedFiles);
            }
        }

        em.refresh(saleItem);
        return saleItemMapper.toDetailResponse(saleItem);
    }

    @Transactional
    public SaleItemDetailResponse updateSaleItem(Integer id, SaleItemUpdateRequest item) {
        SaleItem existing = saleItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SaleItem not found with id :: " + id));

        // Update basic fields
        BrandResponse brandDto = brandService.getBrandById(item.getBrand().getId());
        Brand brand = brandMapper.toEntity(brandDto);
        existing.setBrand(brand);
        existing.setModel(item.getModel());
        existing.setPrice(item.getPrice());
        existing.setDescription(item.getDescription());
        existing.setRamGb(item.getRamGb());
        existing.setScreenSizeInch(item.getScreenSizeInch());
        existing.setStorageGb(item.getStorageGb());
        existing.setColor(item.getColor());
        existing.setQuantity(item.getQuantity());

        List<SaleItemImageRequest> imageInfos = Optional.ofNullable(item.getImageInfos())
                .orElse(new ArrayList<>());

        List<FileEntity> existingFiles = fileService.getFilesByReference("SALE_ITEM", id);
        List<FileEntity> newFilesToStore = new ArrayList<>();
        Set<String> providedFileNames = new HashSet<>();

        if (imageInfos == null || imageInfos.isEmpty()) {
            // Remove all images
            existingFiles.forEach(f -> fileService.deleteFileById(f.getId()));
            existing.setFiles(new ArrayList<>());
            System.out.println("Removed all files");
        } else {
            if (imageInfos.size() > 4) {
                throw new FileUploadException("Maximum 4 images are allowed.");
            }

            // Collect fileNames of existing images to keep
            for (SaleItemImageRequest info : imageInfos) {
                if (info.getFileName() != null) {
                    providedFileNames.add(info.getFileName());
                }
            }

            // Delete any existing file not in provided list
            existingFiles.stream()
                    .filter(f -> !providedFileNames.contains(f.getStoredFilename()))
                    .forEach(f -> fileService.deleteFileById(f.getId()));

            // Process new files
            for (SaleItemImageRequest info : imageInfos) {
                if (info.getImageFile() != null) {
                    if (info.getImageFile().getSize() > 2 * 1024 * 1024) {
                        throw new FileUploadException("Each image must be smaller than 2MB.");
                    }

                    FileEntity newFile = fileService.uploadSingleFile( info.getImageFile(), "SALE_ITEM", id,info.getOrder());
                    newFilesToStore.add(newFile);
                }
            }

            // Update order for existing images
            for (FileEntity file : existingFiles) {
                if (providedFileNames.contains(file.getStoredFilename())) {
                    imageInfos.stream()
                            .filter(i -> file.getStoredFilename().equals(i.getFileName()))
                            .findFirst()
                            .ifPresent(i -> file.setDisplayOrder(i.getOrder()));
                }
            }

            // Merge existing + new files
            List<FileEntity> updatedFileList = new ArrayList<>();
            updatedFileList.addAll(existingFiles.stream()
                    .filter(f -> providedFileNames.contains(f.getStoredFilename()))
                    .toList());
            updatedFileList.addAll(newFilesToStore);

            existing.setFiles(updatedFileList.stream()
                    .sorted(Comparator.comparing(FileEntity::getDisplayOrder))
                    .collect(Collectors.toList()));
        }

        return saleItemMapper.toDetailResponse(existing);
    }

    @Transactional
    public void deleteSaleItemById(Integer id) {
        if (!saleItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Sale item with ID " + id + " not found");
        }

        List<FileEntity> files = fileService.getFilesByReference("SALE_ITEM", id);

        for (FileEntity file : files) {
            fileService.deleteFileById(file.getId());
        }

        saleItemRepository.deleteById(id);
    }

    @Transactional
    public PaginateResponse<SaleItemDetailResponse> getSaleItems(SaleItemPaginationRequest request) {
        Pageable pageable = buildPageable(request);
        Specification<SaleItem> spec = buildSpecification(request);

        Page<SaleItem> saleItems;

        if (spec != null) {
            saleItems = saleItemRepository.findAll(spec, pageable);
        } else if (request.getFilterBrands() != null && !request.getFilterBrands().isEmpty()) {
            saleItems = saleItemRepository.findByBrand_NameIn(request.getFilterBrands(), pageable);
        } else {
            saleItems = saleItemRepository.findAll(pageable);
        }

        Page<SaleItemDetailResponse> dtoPage = saleItems.map(saleItemMapper::toDetailResponse);
        return PaginationUtils.toPaginateResponse(dtoPage);
    }

    public List<Integer> getDistinctStorageSizes(Boolean includeNotSpecified) {
        // ✅ แยกตาม includeNotSpecified
        List<Integer> rawSizes = Boolean.TRUE.equals(includeNotSpecified)
                ? saleItemRepository.findDistinctStorageGbIncludingNull()
                : saleItemRepository.findDistinctStorageGb();

        // ✅ แปลง null เป็น -1 เพื่อให้ frontend แสดง 'Not specified'
        List<Integer> storageSizes = rawSizes.stream()
                .map(s -> s == null ? -1 : s)
                .distinct()
                .collect(Collectors.toList());

        // ✅ ใส่ 32 GB เข้าไปถ้าหาย (ตาม business rule)
        if (!storageSizes.contains(32)) {
            storageSizes.add(32);
        }

        // ✅ เรียง: ค่าจริงมาก่อน แล้วค่อย -1 (Not specified)
        Collections.sort(storageSizes, (a, b) -> {
            if (a == -1) return 1;
            if (b == -1) return -1;
            return Integer.compare(a, b);
        });

        return storageSizes;
    }

    private Pageable buildPageable(SaleItemPaginationRequest request) {
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

    private Specification<SaleItem> buildSpecification(SaleItemPaginationRequest request) {
        Specification<SaleItem> spec = Specification.where(null);

        boolean hasBrand = request.getFilterBrands() != null && !request.getFilterBrands().isEmpty();
        boolean hasStorage = request.getFilterStorages() != null && !request.getFilterStorages().isEmpty();
        boolean hasLowerOnly = request.getFilterPriceLower() != null && request.getFilterPriceUpper() == null;
        boolean hasRange = request.getFilterPriceLower() != null && request.getFilterPriceUpper() != null;
        boolean hasKeyword = request.getFilterSearch() != null && !request.getFilterSearch() .trim().isEmpty();


        if (!hasBrand && !hasStorage && !hasLowerOnly && !hasRange && !hasKeyword) {
            return null; // no filters → no spec
        }

        if (hasKeyword) {
            String normalized = request.getFilterSearch()
                    .trim()
                    .replaceAll("[^\\p{L}\\p{Nd}\\s]", "") // keep only letters, numbers, spaces
                    .replaceAll("\\s+", " ");

            String[] tokens = normalized.toLowerCase().split(" ");

            spec = spec.and((root, query, cb) -> {
                List<Predicate> andPredicates = new ArrayList<>();

                for (String token : tokens) {
                    String kw = "%" + token + "%";

                    // token must appear in at least one field
                    Predicate perToken = cb.or(
                            cb.like(cb.lower(root.get("model")), kw),
                            cb.like(cb.lower(root.get("description")), kw),
                            cb.like(cb.lower(root.get("color")), kw)
                    );

                    // add to AND group
                    andPredicates.add(perToken);
                }

                // require all tokens to match
                return cb.and(andPredicates.toArray(new Predicate[0]));
            });
        }

        if (hasBrand) {
            spec = spec.and((root, q, cb) -> root.get("brand").get("name").in(request.getFilterBrands()));
        }

        if (hasStorage) {
            boolean includeNull = request.getFilterStorages().contains(-1);
            List<Integer> normalStorages = request.getFilterStorages().stream()
                    .filter(v -> v != null && v != -1)
                    .toList();

            Specification<SaleItem> storageSpec = null;
            if (!normalStorages.isEmpty()) {
                storageSpec = (root, q, cb) -> root.get("storageGb").in(normalStorages);
            }
            if (includeNull) {
                Specification<SaleItem> nullSpec = (root, q, cb) -> cb.isNull(root.get("storageGb"));
                storageSpec = (storageSpec == null) ? nullSpec : storageSpec.or(nullSpec);
            }
            if (storageSpec != null) {
                spec = spec.and(storageSpec);
            }
        }

        if (hasLowerOnly) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("price"), request.getFilterPriceLower()));
        } else if (hasRange) {
            spec = spec.and((root, q, cb) -> cb.between(root.get("price"), request.getFilterPriceLower(), request.getFilterPriceUpper()));
        }

        return spec;
    }

}