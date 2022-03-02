package se.epochtimes.backend.images.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.client.TextService;
import se.epochtimes.backend.images.dto.MetaDTO;
import se.epochtimes.backend.images.exception.*;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.HeaderComponent;
import se.epochtimes.backend.images.model.Image;
import se.epochtimes.backend.images.repository.ImageRepository;

import java.io.IOException;
import java.util.stream.Stream;

import static org.apache.http.entity.ContentType.*;

@Service("imageService")
public class ImageService {

  private final TextService textService;
  private final AmazonS3 s3;
  private final ImageRepository repository;

  @Autowired
  public ImageService(TextService textService, AmazonS3 s3, ImageRepository repository) {
    this.textService = textService;
    this.s3 = s3;
    this.repository = repository;
  }

  public MetaDTO save(HeaderComponent hc, BucketName bucketName, MultipartFile file) {
    if(file.isEmpty()) {
      throw new EmptyFileException("Cannot upload empty file [ " + file.getSize() + "]!");
    }
    if(!(isImage(file.getContentType()))) {
      throw new NotAnImageException("File must be an image!");
    }
    String header = hc.vignette().toLowerCase()  + "/" + hc.subYear() + "/" +
      hc.subject().toLowerCase() + "/" + hc.articleId();
    if(!textService.isArticleAvailable(header))
      throw new ArticleNotFoundException("Article " + header + "was not found");
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
    } catch (IOException e) {
      throw new FileReadingException("Problem with reading file", e);
    }
    var i = repository.save(new Image(por.getContentMd5(), por.getETag(), por.getVersionId()));
    return new MetaDTO(i.getTime(), i.getContentMd5(), i.getETag(), i.getVersionId());
  }

  private boolean isImage(String contentType) {
    return Stream.of(IMAGE_JPEG, IMAGE_BMP, IMAGE_GIF,
        IMAGE_PNG, IMAGE_SVG, IMAGE_TIFF, IMAGE_WEBP)
      .anyMatch(i -> (i.toString().equalsIgnoreCase(contentType)));
  }
}
