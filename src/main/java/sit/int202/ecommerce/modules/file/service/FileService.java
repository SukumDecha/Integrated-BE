package sit.int202.ecommerce.modules.file.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sit.int202.ecommerce.common.exceptions.FileUploadException;
import sit.int202.ecommerce.modules.file.model.FileEntity;
import sit.int202.ecommerce.modules.file.repository.FileRepository;
import sit.int202.ecommerce.modules.file.storage.FileStorageAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FileStorageAdapter storageAdapter;

    public List<FileEntity> getFilesByRef(String refType, Integer refId) {
        return fileRepository.findByRefTypeAndRefIdOrderByDisplayOrderAsc(refType, refId);
    }

    public List<FileEntity> uploadFiles(List<MultipartFile> multipartFiles, String refType, Integer refId) {
        final int MAX_FILES = 4;
        final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

        if (multipartFiles.size() > MAX_FILES) {
            throw new FileUploadException("Maximum 4 pictures are allowed.");
        }

        List<FileEntity> savedFiles = new ArrayList<>();

        for (int i = 0; i < multipartFiles.size(); i++) {
            MultipartFile multipartFile = multipartFiles.get(i);
            if (multipartFile.getSize() > MAX_FILE_SIZE) {
                throw new FileUploadException("File size must not exceed 2MB: " + multipartFile.getOriginalFilename());
            }

            String originalFilename = multipartFile.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String storedFilename = UUID.randomUUID() + "." + extension;

            String filePath = storageAdapter.store(multipartFile, storedFilename);

            FileEntity newFile = new FileEntity();
            newFile.setRefType(refType);
            newFile.setRefId(refId);
            newFile.setOriginalFilename(originalFilename);
            newFile.setStoredFilename(storedFilename);
            newFile.setMimeType(multipartFile.getContentType());
            newFile.setFileSize(multipartFile.getSize());
            newFile.setFilePath(filePath);
            newFile.setDisplayOrder(i);
            newFile.setCreatedOn(LocalDateTime.now(ZoneId.of("Asia/Bangkok")));


            savedFiles.add(fileRepository.save(newFile));
        }

        return savedFiles;
    }

    public FileEntity saveFile(MultipartFile multipartFile, String refType, Integer refId, Integer order) throws IOException {
        String extension = getFileExtension(multipartFile.getOriginalFilename());
        String storedFilename = UUID.randomUUID() + "." + extension;

        storageAdapter.saveFile(multipartFile, refType, storedFilename);

        var file = new FileEntity();
        file.setRefType(refType);
        file.setRefId(refId);
        file.setOriginalFilename(multipartFile.getOriginalFilename());
        file.setStoredFilename(storedFilename);
        file.setMimeType(multipartFile.getContentType());
        file.setFileSize(multipartFile.getSize());
        file.setFilePath(storageAdapter.getFilePath(storedFilename, refType));
        file.setDisplayOrder(order != null ? order : 0);
        file.setCreatedOn(LocalDateTime.now(ZoneId.of("Asia/Bangkok")));

        return fileRepository.save(file);
    }

    @Transactional
    public boolean deleteFile(Integer fileId) {
        return fileRepository.findById(fileId).map(file -> {
            try {
                System.out.println("⛔ Attempting to delete file metadata and disk file:");
                System.out.println(" - fileId: " + fileId);
                System.out.println(" - storedFilename: " + file.getStoredFilename());
                System.out.println(" - refType (used as subDirectory): " + file.getRefType());
                fileRepository.delete(file);

                boolean deletedFromDisk = storageAdapter.deleteFile(file.getStoredFilename(), file.getRefType());

                if (!deletedFromDisk) {
                    System.err.println("️File not found on disk, but metadata deleted: " + file.getStoredFilename());
                }

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }).orElse(false);
    }

    public List<FileEntity> getFilesByRefAndUsage(String refType, Integer refId) {
        return fileRepository.findByRefTypeAndRefIdOrderByDisplayOrderAsc(refType, refId);
    }

    public String getFileExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }

    public FileEntity storeFile(String refType, Integer refId, MultipartFile multipartFile, Integer displayOrder) throws IOException {
        String extension = getFileExtension(multipartFile.getOriginalFilename());
        String storedFilename = UUID.randomUUID() + "." + extension;
        String filePath = storageAdapter.store(multipartFile, storedFilename);

        FileEntity file = new FileEntity();
        file.setRefType(refType);
        file.setRefId(refId);
        file.setOriginalFilename(multipartFile.getOriginalFilename());
        file.setStoredFilename(storedFilename);
        file.setMimeType(multipartFile.getContentType());
        file.setFileSize(multipartFile.getSize());
        file.setFilePath(filePath);
        file.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        file.setCreatedOn(LocalDateTime.now(ZoneId.of("Asia/Bangkok")));


        return fileRepository.save(file);
    }

    public FileEntity getFileByStoredFilename(String storedFilename) {
        return fileRepository.findByStoredFilename(storedFilename)
                .orElse(null); // หรือ throw exception ถ้าจำเป็น
    }

    public String convertToBase64(String storedFilename) {
        try {
            Path path = Paths.get("uploads", storedFilename);
            byte[] bytes = Files.readAllBytes(path);
            String mimeType = Files.probeContentType(path);
            return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            log.warn("Failed to convert to base64 for: {}", storedFilename);
            return null;
        }
    }



}