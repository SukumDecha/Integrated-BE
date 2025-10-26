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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final FileStorageAdapter storageAdapter;

    private static final int MAX_FILES = 4;
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final ZoneId ZONE = ZoneId.of("Asia/Bangkok");

    @Override
    public List<FileEntity> getFilesByReference(String refType, Integer refId) {
        return fileRepository.findByRefTypeAndRefIdOrderByDisplayOrderAsc(refType, refId);
    }

    @Override
    public List<FileEntity> uploadMultipleFiles(List<MultipartFile> multipartFiles, String refType, Integer refId, String usageType) {
        if (multipartFiles.size() > MAX_FILES) {
            throw new FileUploadException("Maximum " + MAX_FILES + " files are allowed.");
        }

        List<FileEntity> savedFiles = new ArrayList<>();
        for (int i = 0; i < multipartFiles.size(); i++) {
            MultipartFile file = multipartFiles.get(i);

            if (file.getSize() > MAX_FILE_SIZE) {
//                throw new FileUploadException("File exceeds 2MB: " + file.getOriginalFilename());
                continue;
            }

            savedFiles.add(storeFileAndMetadata(file, refType, refId, i, usageType));
        }

        return savedFiles;
    }

    @Override
    public FileEntity uploadSingleFile(MultipartFile multipartFile, String refType, Integer refId, Integer order, String usageType) {
        return storeFileAndMetadata(multipartFile, refType, refId, order, usageType);
    }

    @Override
    @Transactional
    public boolean deleteFileById(Integer fileId) {
        return fileRepository.findById(fileId)
                .map(file -> {
                    try {
                        fileRepository.delete(file);
                        boolean deletedFromDisk = storageAdapter.deleteFile(file.getStoredFilename(), file.getRefType());

                        if (!deletedFromDisk) {
                            log.warn("File metadata deleted but file not found on disk: {}", file.getStoredFilename());
                        }
                        return true;
                    } catch (IOException e) {
                        log.error("Error deleting file from disk: {}", file.getStoredFilename(), e);
                        return false;
                    }
                }).orElse(false);
    }

    /* ------------------- Helper Methods ------------------- */
    private FileEntity storeFileAndMetadata(MultipartFile multipartFile, String refType, Integer refId, Integer displayOrder, String usageType) {
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String extension = extractFileExtension(originalFilename);
            String storedFilename = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);

            String filePath = storageAdapter.storeFile(multipartFile, refType, storedFilename);
            return createFileEntity(multipartFile, refType, refId, storedFilename, filePath, displayOrder, usageType);
        } catch (IOException e) {
            log.error("Failed to store file: {}", multipartFile.getOriginalFilename(), e);
            throw new FileUploadException("Failed to store file: " + multipartFile.getOriginalFilename());
        }
    }

    private FileEntity createFileEntity(MultipartFile multipartFile, String refType, Integer refId,
                                        String storedFilename, String filePath, Integer displayOrder, String usageType) {
        var fileEntity = new FileEntity();
        fileEntity.setRefType(refType);
        fileEntity.setRefId(refId);
        fileEntity.setUsageType(usageType);
        fileEntity.setOriginalFilename(multipartFile.getOriginalFilename());
        fileEntity.setStoredFilename(storedFilename);
        fileEntity.setMimeType(multipartFile.getContentType());
        fileEntity.setFileSize(multipartFile.getSize());
        fileEntity.setFilePath(filePath);
        fileEntity.setDisplayOrder(displayOrder != null ? displayOrder : 0);

        return fileRepository.save(fileEntity);
    }

    private String extractFileExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1 || dotIndex == filename.length() - 1) ?
                "" : filename.substring(dotIndex + 1).toLowerCase();
    }
}