package se.epochtimes.backend.images.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.dto.FileDTO;
import se.epochtimes.backend.images.exception.AlreadyAddedException;
import se.epochtimes.backend.images.exception.EmptyFileException;
import se.epochtimes.backend.images.exception.FileReadingException;
import se.epochtimes.backend.images.exception.NotAnImageException;
import se.epochtimes.backend.images.model.File;
import se.epochtimes.backend.images.repository.file.FileRepository;
import se.epochtimes.backend.images.repository.image.ImageRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.http.entity.ContentType.*;

@Service("imageService")
public class ImageService {

  private final ImageRepository imageRepository;
  private final FileRepository fileRepository;

  @Autowired
  public ImageService(ImageRepository imageRepo, FileRepository metaRepo) {
    this.imageRepository = imageRepo;
    this.fileRepository = metaRepo;
  }

  public FileDTO save(String header, MultipartFile file) {
    validate(file);
    String filePath = header + "/" + file.getOriginalFilename();
    if(fileRepository.existsByFilePath(filePath))
      throw new AlreadyAddedException("Image with path " + filePath + " has already been added");
    File m = persist(header, file);
    return new FileDTO(m.getTime(), m.getFilePath(), m.getMeta());
  }

  private File persist(String header, MultipartFile file) {
    File meta;
    try{
      meta = imageRepository.save(header, file);
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

  public List<FileDTO> getAllUnsorted() {
    return fileRepository.findAll().stream().map(FileDTO::new).collect(Collectors.toList());
  }

  public byte[] get(String header, String fileName) {
    String filePath = header + "/" + fileName;
    try {
      return imageRepository.download(filePath);
    } catch (IOException e) {
      throw new FileReadingException("Problem with reading file", e);
    }
  }

  public void deleteByFilePath(String filePath) {
    List<File> filesWithPath = fileRepository.findByFilePath(filePath);
    fileRepository.deleteAll(filesWithPath);
  }

  public void deleteAll() {
    fileRepository.deleteAll();
  }
}
