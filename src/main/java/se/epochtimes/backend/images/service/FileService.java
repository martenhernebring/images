package se.epochtimes.backend.images.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class FileService {

  private final AmazonS3 s3;

  public FileService(AmazonS3 s3) {
    this.s3 = s3;
  }

  public void save(String bucketName,
                   MultipartFile file,
                   String keyName) {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    try{
      s3.putObject(bucketName, keyName, file.getInputStream(), metadata);
    } catch(AmazonServiceException e) {
      throw new IllegalStateException("Failed to store file to s3", e);
    } catch (IOException e) {
      throw new IllegalStateException("Problem with reading file", e);
    }
  }
}
