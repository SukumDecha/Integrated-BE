package sit.int202.ecommerce.modules.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int202.ecommerce.modules.file.model.File;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {

    List<File> findByRefTypeAndRefIdOrderByDisplayOrderAsc(String refType, Integer refId);



    void deleteByRefTypeAndRefId(String refType, Integer refId);

    Optional<File> findByStoredFilename(String storedFilename);
}