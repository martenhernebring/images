package se.epochtimes.backend.images.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.epochtimes.backend.images.model.File;

@Repository("fileRepository")
public interface FileRepository extends JpaRepository<File, Long> {
}
