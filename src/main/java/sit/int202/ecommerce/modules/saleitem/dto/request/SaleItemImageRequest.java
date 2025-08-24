package sit.int202.ecommerce.modules.saleitem.dto.request;


import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SaleItemImageRequest {
    private Integer order;              // ลำดับแสดงภาพ
    private String fileName;           // ชื่อไฟล์เดิม (ใช้สำหรับอ้างอิงภาพเก่า)
    private String status;             // "NEW", "EXISTING", "DELETED"
    private MultipartFile imageFile;   // ไฟล์ที่แนบมา
}
