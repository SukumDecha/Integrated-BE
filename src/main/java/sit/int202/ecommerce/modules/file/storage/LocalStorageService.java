package sit.int202.ecommerce.modules.file.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class LocalStorageService implements FileStorageAdapter {

    @Value("${app.file.upload-dir}")
    private String uploadDir; // อ่านค่าจาก application.properties หรือ .env เช่น "uploads"

    // ใช้สำหรับเก็บไฟล์ทั่วไป (ไม่ระบุหมวด)
    @Override
    public String store(MultipartFile file, String storedFilename) {
        try {
            Path destination = Paths.get(uploadDir, storedFilename);
            Files.createDirectories(destination.getParent()); // สร้างโฟลเดอร์ถ้ายังไม่มี
            file.transferTo(destination.toFile());
            log.info("File stored at: {}", destination.toAbsolutePath());
            return destination.toString(); // คืน path ที่ใช้เก็บไฟล์จริง
        } catch (IOException e) {
            log.error("Failed to store file {}", storedFilename, e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    // ลบไฟล์จาก root directory
    @Override
    public boolean delete(String storedFilename) {
        Path filePath = Paths.get(uploadDir, storedFilename);
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete file {}", storedFilename, e);
            return false;
        }
    }

    // คืน path เต็มจากชื่อไฟล์เฉยๆ (ไม่ระบุหมวด)
    @Override
    public String getStoragePath(String storedFilename) {
        return Paths.get(uploadDir, storedFilename).toString();
    }

    // ใช้สำหรับเก็บไฟล์ใน subdirectory เช่น sale-items/
    @Override
    public String saveFile(MultipartFile file, String subDirectory, String storedFilename) throws IOException {
        Path destination = Paths.get(uploadDir, subDirectory, storedFilename);
        Files.createDirectories(destination.getParent());
        file.transferTo(destination.toFile());
        log.info("File stored at: {}", destination.toAbsolutePath());
        return destination.toString();
    }

    @Override
    public boolean deleteFile(String storedFilename, String subDirectory) throws IOException {
        Path filePath = Paths.get(uploadDir, subDirectory, storedFilename);
        try {
            System.out.println("🧾 Trying to delete file from disk at path: " + filePath.toAbsolutePath());
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete file {}/{}", subDirectory, storedFilename, e);
            return false;
        }
    }

    @Override
    public String getFilePath(String storedFilename, String subDirectory) {
        return Paths.get(uploadDir, subDirectory, storedFilename).toString();
    }
}