package sit.int202.ecommerce.modules.brand.service;

import sit.int202.ecommerce.modules.brand.dto.request.BrandCreateRequest;
import sit.int202.ecommerce.modules.brand.dto.request.BrandUpdateRequest;
import sit.int202.ecommerce.modules.brand.dto.response.BrandDetailResponse;
import sit.int202.ecommerce.modules.brand.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {

    List<BrandResponse> getAllBrands();

    BrandDetailResponse getBrandById(Integer id);

    BrandDetailResponse createBrand(BrandCreateRequest request);

    BrandDetailResponse updateBrand(Integer id, BrandUpdateRequest payload);

    void deleteBrandById(Integer id);
}
