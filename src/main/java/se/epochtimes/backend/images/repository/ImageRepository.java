package se.epochtimes.backend.images.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.Meta;

import java.io.IOException;

public interface ImageRepository {
  Meta save(BucketName bucketName, String header, MultipartFile file) throws IOException;
}
