package se.epochtimes.backend.images.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.model.Meta;
import se.epochtimes.backend.images.repository.ImageRepository;
import se.epochtimes.backend.images.dto.MetaDTO;
import se.epochtimes.backend.images.exception.*;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.HeaderComponent;
import se.epochtimes.backend.images.repository.MetaRepository;
import se.epochtimes.backend.images.repository.TextClient;
import se.epochtimes.backend.images.repository.TextRepository;

import java.io.IOException;
import java.util.stream.Stream;

import static org.apache.http.entity.ContentType.*;

@Service("imageService")
public class ImageService {

  private final TextRepository textRepository;
  private final ImageRepository imageRepository;
  private final MetaRepository metaRepository;

  @Autowired
  public ImageService(TextRepository textRepo, ImageRepository imageRepo, MetaRepository metaRepo) {
    this.textRepository = textRepo;
    this.imageRepository = imageRepo;
    this.metaRepository = metaRepo;
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
    if(!textRepository.isArticleAvailable(header))
      throw new ArticleNotFoundException("Article " + header + "was not found");
    Meta meta;
    try{
      meta = imageRepository.save(bucketName, header, file);
    } catch (IOException e) {
      throw new FileReadingException("Problem with reading file", e);
    }
    Meta m = metaRepository.save(meta);
    return new MetaDTO(m.getTime(), m.getContentMd5(), m.getETag(), m.getVersionId());
  }

  private boolean isImage(String contentType) {
    return Stream.of(IMAGE_JPEG, IMAGE_BMP, IMAGE_GIF,
        IMAGE_PNG, IMAGE_SVG, IMAGE_TIFF, IMAGE_WEBP)
      .anyMatch(i -> (i.toString().equalsIgnoreCase(contentType)));
  }
}
