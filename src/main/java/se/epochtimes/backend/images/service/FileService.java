package se.epochtimes.backend.images.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.HeaderComponent;

import java.io.IOException;

@Service
public class FileService {

  private final AmazonS3 s3;

  public FileService(AmazonS3 s3) {
    this.s3 = s3;
  }

  public void save(HeaderComponent hc, BucketName bucketName, MultipartFile file) {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    String fileName = hc.subject().getPrint() + "/" + hc.subYear() +
      "/" + hc.vignette()  + "/" + file.getName();
    try{
      s3.putObject(bucketName.getBucketName(), fileName,
        file.getInputStream(), metadata);
    } catch(AmazonServiceException e) {
      throw new IllegalStateException("Failed to store file to s3", e);
    } catch (IOException e) {
      throw new IllegalStateException("Problem with reading file", e);
    }
  }
}
