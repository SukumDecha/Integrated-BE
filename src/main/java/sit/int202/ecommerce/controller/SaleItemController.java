package sit.int202.ecommerce.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.int202.ecommerce.model.SaleItem;
import sit.int202.ecommerce.service.SaleItemService;

@RestController
@RequestMapping("/v1/sale-item")
public class SaleItemController {

    private final SaleItemService saleItemService;

    public SaleItemController(SaleItemService saleItemService) {
        this.saleItemService = saleItemService;
    }

    @GetMapping ("/{id}")
    public ResponseEntity<SaleItem> getSimpleSaleItem(@PathVariable Integer id) {
        var saleitem = saleItemService.getSaleItemById(id);
        return ResponseEntity.ok(saleitem);
    }


}
