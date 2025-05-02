package sit.int202.ecommerce.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class SaleItemResponse {
    private Integer id;
    private String model;
    private String description;
    private Integer price;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer storageGb;
    private String color;
    private Integer quantity;
    private Instant createdOn;
    private Instant updatedOn;
    private String brandName; // เพิ่มสำหรับ mapping ข้อมูลจาก Brand
}

