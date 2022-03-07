package se.epochtimes.backend.images.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.exception.StorageFailureException;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.File;
import se.epochtimes.backend.images.model.file.Meta;

import java.io.IOException;

@Repository("imageRepository")
public class ImageStorage implements ImageRepository {

  private final AmazonS3 amazonS3;

  @Autowired
  public ImageStorage(AmazonS3 amazonS3) {
    this.amazonS3 = amazonS3;
  }

  @Override
  public File save(BucketName bucketName, String header, MultipartFile file) throws IOException {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(file.getContentType());
    metadata.setContentLength(file.getSize());
    String filePath = header + "/" + file.getOriginalFilename();
    PutObjectResult por;
    try {
      por = amazonS3.putObject(bucketName.getBucketName(), filePath,
        file.getInputStream(), metadata);
    } catch (AmazonServiceException e) {
      throw new StorageFailureException("Failed to store file to s3", e);
    }
    Meta meta = new Meta(por.getContentMd5(), por.getETag(), por.getVersionId());
    return new File(filePath, meta);
  }
}
