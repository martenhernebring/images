package se.epochtimes.backend.images.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.dto.FileDTO;
import se.epochtimes.backend.images.exception.StorageFailureException;
import se.epochtimes.backend.images.model.File;
import se.epochtimes.backend.images.model.file.Meta;
import se.epochtimes.backend.images.model.multipart.CorrectMultiPart;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static se.epochtimes.backend.images.model.BucketName.ARTICLE_IMAGE;

@ExtendWith(MockitoExtension.class)
class ImageRepositoryTest {

  @Mock
  AmazonS3 amazonS3;

  @InjectMocks
  ImageStorage imageRepository;

  @Test
  void shouldThrowStorageFailureException() {
    when(amazonS3.putObject(any(String.class),
      any(String.class), any(InputStream.class), any(ObjectMetadata.class)))
      .thenThrow(new AmazonServiceException("Test"));
    String header = "ekonomi/2022/inrikes/1617";
    assertThrows(StorageFailureException.class,
      () -> imageRepository.save(ARTICLE_IMAGE, header, new CorrectMultiPart()));
  }

  @Test
  void shouldNotThrowWhenSaving() {
    PutObjectResult por = new PutObjectResult();
    por.setContentMd5("sWSbvU0leS0QWOzgB5xIyw==");
    por.setETag("b1649bbd4d25792d1058ece0079c48cb");
    por.setVersionId("cPXs4Kq0FQhbnSl0IGNXMEPA4NLRIfGj");
    File fileData = new File();
    fileData.setId(1234L);
    assertEquals(1234L, fileData.getId());
    fileData.setTime();
    Meta meta = new Meta();
    meta.setContentMd5(por.getContentMd5());
    meta.setETag(por.getETag());
    meta.setVersionId(por.getVersionId());
    fileData.setMeta(meta);
    String header = "ekonomi/2022/inrikes/1617";
    MultipartFile file = new CorrectMultiPart();
    fileData.setFilePath(header + file.getName());
    when(amazonS3.putObject(any(String.class), any(String.class), any(InputStream.class), any(ObjectMetadata.class)))
      .thenReturn(por);

    File result = null;
    try {
      result = imageRepository.save(ARTICLE_IMAGE, header, file);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    FileDTO expected = new FileDTO(OffsetDateTime.now(), result.getFilePath(), result.getMeta());
    assertEquals(expected.meta().getContentMd5(), result.getMeta().getContentMd5());
    assertEquals(expected.meta().getETag(), result.getMeta().getETag());
    assertEquals(expected.meta().getVersionId(), result.getMeta().getVersionId());
  }
}