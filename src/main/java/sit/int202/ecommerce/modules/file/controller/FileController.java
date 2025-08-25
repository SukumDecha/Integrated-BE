package sit.int202.ecommerce.modules.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int202.ecommerce.modules.file.model.FileEntity;
import sit.int202.ecommerce.modules.file.service.FileServiceImpl;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileServiceImpl fileService;

    // ✅ POST: อัปโหลดหลายไฟล์
    @PostMapping
    public ResponseEntity<?> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("refType") String refType,
            @RequestParam("refId") Integer refId
    ) {
        return ResponseEntity.ok(fileService.uploadMultipleFiles(files, refType, refId));
    }

    // ✅ DELETE: ลบไฟล์จาก id
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Integer fileId) {
        boolean deleted = fileService.deleteFileById(fileId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // ✅ GET: ดึงรายการไฟล์ของ entity (sale item, brand, user, etc.)
    @GetMapping
    public ResponseEntity<List<FileEntity>> getFilesByRef(
            @RequestParam("refType") String refType,
            @RequestParam("refId") Integer refId
    ) {
        return ResponseEntity.ok(fileService.getFilesByReference(refType, refId));
    }
}