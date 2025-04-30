package sit.int202.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.model.Brand;
import sit.int202.ecommerce.repository.BrandRepository;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public void save(Brand brand) {
        brandRepository.save(brand);
    }
}
