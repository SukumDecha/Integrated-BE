package sit.int202.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.int202.ecommerce.dto.response.BrandResponse;
import sit.int202.ecommerce.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/v1/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    public List<BrandResponse> getAllBrands() {
        return brandService.getAllBrands();
    }
}
