package se.epochtimes.backend.images.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import se.epochtimes.backend.images.dto.MetaDTO;
import se.epochtimes.backend.images.exception.EmptyFileException;
import se.epochtimes.backend.images.exception.FileReadingException;
import se.epochtimes.backend.images.exception.NotAnImageException;
import se.epochtimes.backend.images.exception.StorageFailureException;
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

  public MetaDTO save(HeaderComponent hc, BucketName bucketName, MultipartFile file) {
    if(file.isEmpty()) {
      throw new EmptyFileException("Cannot upload empty file [ " + file.getSize() + "]!");
    }
    if(!(isImage(file.getContentType()))) {
      throw new NotAnImageException("File must be an image!");
    }
    String baseUrl = "http://localhost:8181/v1/articles/";
    String header = hc.vignette().toLowerCase()  + "/" + hc.subYear() + "/" +
      hc.subject().getPrint().toLowerCase() + "/" + hc.articleId();
    WebClient client = WebClient.builder().baseUrl(baseUrl).build();
    client
      .get()
      .uri(header)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .toBodilessEntity()
      .block();
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(file.getContentType());
    metadata.setContentLength(file.getSize());
    String fileName =  header + "/"  + file.getName();
    PutObjectResult por;
    try{
      por = s3.putObject(bucketName.getBucketName(), fileName,
        file.getInputStream(), metadata);
    } catch(AmazonServiceException e) {
      throw new StorageFailureException("Failed to store file to s3", e);
    } catch (IOException | WebClientRequestException e) {
      throw new FileReadingException("Problem with reading file", e);
    }
    return new MetaDTO(por.getContentMd5(), por.getETag(), por.getVersionId());
  }

  private boolean isImage(String contentType) {
    return Stream.of(IMAGE_JPEG, IMAGE_BMP, IMAGE_GIF,
        IMAGE_PNG, IMAGE_SVG, IMAGE_TIFF, IMAGE_WEBP)
      .anyMatch(i -> (i.toString().equalsIgnoreCase(contentType)));
  }
}
