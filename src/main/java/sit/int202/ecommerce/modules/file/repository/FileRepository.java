package sit.int202.ecommerce.modules.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int202.ecommerce.modules.file.model.FileEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Integer> {

    List<FileEntity> findByRefTypeAndRefIdOrderByDisplayOrderAsc(String refType, Integer refId);

    Optional<FileEntity> findByStoredFilename(String storedFilename);

    void deleteByRefTypeAndRefId(String refType, Integer refId);
}