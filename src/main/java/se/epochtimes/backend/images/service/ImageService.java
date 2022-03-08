package se.epochtimes.backend.images.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.dto.FileDTO;
import se.epochtimes.backend.images.exception.*;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.File;
import se.epochtimes.backend.images.repository.FileRepository;
import se.epochtimes.backend.images.repository.ImageRepository;
import se.epochtimes.backend.images.repository.TextRepository;

import java.io.IOException;
import java.util.stream.Stream;

import static org.apache.http.entity.ContentType.*;

@Service("imageService")
public class ImageService {

  private final TextRepository textRepository;
  private final ImageRepository imageRepository;
  private final FileRepository fileRepository;

  @Autowired
  public ImageService(TextRepository textRepo, ImageRepository imageRepo, FileRepository metaRepo) {
    this.textRepository = textRepo;
    this.imageRepository = imageRepo;
    this.fileRepository = metaRepo;
  }

  public FileDTO save(String header, BucketName bucketName, MultipartFile file) {
    validate(file);
    String filePath = header + "/" + file.getOriginalFilename();
    if(fileRepository.existsByFilePath(filePath))
      throw new AlreadyAddedException("Image with path " + filePath + " has already been added");
    if(!textRepository.isArticleAvailable(header))
      throw new ArticleNotFoundException("Article " + header + "was not found");
    File m = persist(bucketName, header, file);
    return new FileDTO(m.getTime(), m.getFilePath(), m.getMeta());
  }

  private File persist(BucketName bucketName, String header, MultipartFile file) {
    File meta;
    try{
      meta = imageRepository.save(bucketName, header, file);
    } catch (IOException e) {
      throw new FileReadingException("Problem with reading file", e);
    }
    return fileRepository.save(meta);
  }

  private void validate(MultipartFile file) {
    if(file.isEmpty()) {
      throw new EmptyFileException("Cannot upload empty file [ " + file.getSize() + "]!");
    }
    if(!(isImage(file.getContentType()))) {
      throw new NotAnImageException("File must be an image!");
    }
  }

  private boolean isImage(String contentType) {
    return Stream.of(IMAGE_JPEG, IMAGE_BMP, IMAGE_GIF,
        IMAGE_PNG, IMAGE_SVG, IMAGE_TIFF, IMAGE_WEBP)
      .anyMatch(i -> (i.toString().equalsIgnoreCase(contentType)));
  }
}
