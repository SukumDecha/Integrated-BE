package sit.int202.ecommerce.dto;

import lombok.Data;


import java.math.BigDecimal;


@Data
public class SaleItemResponse {
    private Integer id;
    private String brandName;
    private String model;
    private Integer price;
    private String description;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer storageGb;
    private String color;
    private Integer quantity;

}

