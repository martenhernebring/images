package se.epochtimes.backend.images.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.util.IOUtils;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.config.ImageConfiguration;
import se.epochtimes.backend.images.model.File;
import se.epochtimes.backend.images.repository.image.ImageRepository;
import se.epochtimes.backend.images.repository.image.ImageStorage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ImageConfiguration.class)
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
public class ImageStorageIT {

  @Autowired
  private ImageConfiguration imageConfiguration;

  @Test
  void downloadFileInS3() throws IOException {
    AmazonS3 s3Client = imageConfiguration.amazonS3();
    ImageRepository imageRepository = new ImageStorage(s3Client);
    String h = "inrikes/2022/ekonomi/1617/mock.png";
    var result = imageRepository.download(h);
    var original = Files.readString(
      Paths.get("C:\\Users\\HP\\Pictures\\mock.png"),
      StandardCharsets.ISO_8859_1
    );
    String utf8 = new String(original.getBytes("ISO-8859-1"), "UTF-8");
    assertEquals(utf8, new String(result));
  }

  @Test
  @Disabled
  void saveFileInS3() {
    AmazonS3 s3Client = imageConfiguration.amazonS3();
    ImageRepository imageRepository = new ImageStorage(s3Client);
    java.io.File initialFile = null;
    FileInputStream input = null;
    try {
      initialFile = ResourceUtils
        .getFile("classpath:static/images/20220227_143031.jpg");
      input = new FileInputStream(initialFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail();
    }
    MultipartFile multipartFile = null;
    try {
      multipartFile = new MockMultipartFile(initialFile.getName(), initialFile.getName(),
        String.valueOf(ContentType.IMAGE_JPEG), IOUtils.toByteArray(input));
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    String h = "inrikes/2022/ekonomi/1617";
    File file = null;
    try {
      file = imageRepository.save(h, multipartFile);
    } catch (IOException e) {
      fail();
      e.printStackTrace();
    }
    System.out.println(file);
  }
}
