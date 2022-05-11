package se.epochtimes.backend.images.repository.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.epochtimes.backend.images.model.File;

import java.util.List;

@Repository("fileRepository")
public interface FileRepository extends JpaRepository<File, Long> {
  boolean existsByFilePath(String filePath);

  @Query(value = "SELECT * FROM FILE f WHERE f.file_path = ?1", nativeQuery = true)
  List<File> findByFilePath(@Param(value = "filePath") String filePath);
}
