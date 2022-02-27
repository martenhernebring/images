package se.epochtimes.backend.images.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class FileService {

  private final AmazonS3 s3;

  public FileService(AmazonS3 s3) {
    this.s3 = s3;
  }

  public void save(String path,
                   String fileName,
                   InputStream inputStream,
                   Optional<Map<String, String>> optionalMetaData) {
    ObjectMetadata metadata = new ObjectMetadata();
    optionalMetaData.ifPresent(map -> {
      if(!map.isEmpty()) {
        map.forEach(metadata::addUserMetadata);
      }
    });
    try{
      s3.putObject(path, fileName, inputStream, metadata);
    } catch(AmazonServiceException e) {
      throw new IllegalStateException("Failed to store file to s3", e);
    }
  }
}
