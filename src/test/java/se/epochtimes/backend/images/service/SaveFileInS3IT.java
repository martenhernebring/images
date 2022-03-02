package se.epochtimes.backend.images.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.util.IOUtils;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.client.TextService;
import se.epochtimes.backend.images.config.AmazonConfiguration;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.HeaderComponent;
import se.epochtimes.backend.images.repository.ImageRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AmazonConfiguration.class)
@Disabled
public class SaveFileInS3IT {

  @Autowired
  private AmazonConfiguration amazonConfiguration;

  @MockBean
  private ImageRepository mockedRepository;

  @MockBean
  private TextService mockedService;

  @Test
  void saveFileInS3() {
    AmazonS3 s3Client = amazonConfiguration.s3();
    ImageService imageService = new ImageService(mockedService, s3Client, mockedRepository);
    File initialFile = null;
    FileInputStream input = null;
    try {
      initialFile = ResourceUtils.getFile("classpath:static/images/20220227_143031.jpg");
      input = new FileInputStream(initialFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail();
    }
    MultipartFile multipartFile = null;
    try {
      multipartFile = new MockMultipartFile(initialFile.getName(),
        initialFile.getName(), String.valueOf(ContentType.IMAGE_JPEG), IOUtils.toByteArray(input));
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    final HeaderComponent hc = new HeaderComponent(
      "ekonomi", 2022, "inrikes", "1617"
    );
    var meta = imageService.save(hc, BucketName.ARTICLE_IMAGE, multipartFile);
    System.out.println(meta);
  }
}
