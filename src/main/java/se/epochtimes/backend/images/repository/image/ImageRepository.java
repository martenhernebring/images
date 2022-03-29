package se.epochtimes.backend.images.repository.image;

import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.model.File;

import java.io.IOException;

public interface ImageRepository {
  File save(String header, MultipartFile file) throws IOException;
  byte[] download(String filePath) throws IOException;
}
