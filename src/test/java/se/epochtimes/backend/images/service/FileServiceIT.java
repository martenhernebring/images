package se.epochtimes.backend.images.service;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import se.epochtimes.backend.images.bucket.BucketName;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FileServiceIT {

  @Test
  void saveFileInS3() {
    AmazonService amazonService = new AmazonService();
    AmazonS3 s3Client = amazonService.s3();
    FileService fileService = new FileService(s3Client);
    File initialFile = null;
    try {
      initialFile = ResourceUtils.getFile("classpath:static/images/20220227_143031.jpg");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail();
    }
    InputStream inputStream = null;
    try {
      assertNotNull(initialFile);
      inputStream = new FileInputStream(initialFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail();
    }
    Map<String, String> metaData = new HashMap<>();
    metaData.put("location", "Välen");
    assertNotNull(inputStream);
    fileService.save(
      initialFile.getPath(),
      "20220227_143031.jpg",
      inputStream,
      Optional.of(metaData));
    assertTrue(s3Client.doesObjectExist(BucketName.ARTICLE_IMAGE.getBucketName(),
      System.getenv().get("ACCESS_KEY")));
  }
}
