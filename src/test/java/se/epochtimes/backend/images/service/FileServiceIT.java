package se.epochtimes.backend.images.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.util.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.bucket.BucketName;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AmazonConfiguration.class)
public class FileServiceIT {

  @Autowired
  private AmazonConfiguration amazonConfiguration;

  @Test
  void saveFileInS3() {
    AmazonS3 s3Client = amazonConfiguration.s3();
    FileService fileService = new FileService(s3Client);
    File initialFile = null;
    try {
      initialFile = ResourceUtils.getFile("classpath:static/images/20220227_143031.jpg");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail();
    }
    FileInputStream input = null;
    try {
      input = new FileInputStream(initialFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    MultipartFile multipartFile = null;
    try {
      multipartFile = new MockMultipartFile("file",
        initialFile.getName(), "text/plain", IOUtils.toByteArray(input));
    } catch (IOException e) {
      e.printStackTrace();
    }
    fileService.save( BucketName.ARTICLE_IMAGE.getBucketName(), multipartFile,
      initialFile.getName());
    assertTrue(s3Client.doesObjectExist(BucketName.ARTICLE_IMAGE.getBucketName(),
      System.getenv().get("ACCESS_KEY")));
  }
}
