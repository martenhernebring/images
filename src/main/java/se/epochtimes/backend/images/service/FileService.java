package se.epochtimes.backend.images.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import se.epochtimes.backend.images.exception.ArticleNotFound;
import se.epochtimes.backend.images.exception.EmptyFileException;
import se.epochtimes.backend.images.exception.NotAnImageException;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.HeaderComponent;

import java.io.IOException;
import java.util.stream.Stream;

import static org.apache.http.entity.ContentType.*;

@Service
public class FileService {

  private final AmazonS3 s3;

  @Autowired
  public FileService(AmazonS3 s3) {
    this.s3 = s3;
  }

  public void save(HeaderComponent hc, BucketName bucketName, MultipartFile file) {
    if(file.isEmpty()) {
      throw new EmptyFileException("Cannot upload empty file [ " + file.getSize() + "]!");
    }
    if(!(isImage(file.getContentType()))) {
      throw new NotAnImageException("File must be an image!");
    }
    String baseUrl = "http://localhost:8181/v1/articles/";
    String header = hc.vignette().toLowerCase()  + "/" + hc.subYear() + "/" +
      hc.subject().getPrint().toLowerCase() + "/" + 1616;
    WebClient client = WebClient.builder().baseUrl(baseUrl).build();
    client
      .get()
      .uri(header)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .toBodilessEntity()
      .block();
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    String fileName =  header + "/"  + file.getName();
    try{
      s3.putObject(bucketName.getBucketName(), fileName,
        file.getInputStream(), metadata);
    } catch(AmazonServiceException e) {
      throw new IllegalStateException("Failed to store file to s3", e);
    } catch (IOException e) {
      throw new IllegalStateException("Problem with reading file", e);
    }
  }

  private boolean isImage(String contentType) {
    return Stream.of(IMAGE_JPEG, IMAGE_BMP, IMAGE_GIF,
        IMAGE_PNG, IMAGE_SVG, IMAGE_TIFF, IMAGE_WEBP)
      .anyMatch(i -> (i.toString().equalsIgnoreCase(contentType)));
  }
}
