package se.epochtimes.backend.images.repository;

import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.File;

import java.io.IOException;

public interface ImageRepository {
  File save(BucketName bucketName, String header, MultipartFile file) throws IOException;
}
