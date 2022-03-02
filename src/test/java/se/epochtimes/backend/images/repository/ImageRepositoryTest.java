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
import se.epochtimes.backend.images.dto.MetaDTO;
import se.epochtimes.backend.images.exception.StorageFailureException;
import se.epochtimes.backend.images.model.Meta;
import se.epochtimes.backend.images.service.multipart.CorrectMultiPart;

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
    Meta meta = new Meta();
    meta.setId(1234L);
    assertEquals(1234L, meta.getId());
    meta.setTime();
    meta.setContentMd5(por.getContentMd5());
    meta.setETag(por.getETag());
    meta.setVersionId(por.getVersionId());
    when(amazonS3.putObject(any(String.class), any(String.class), any(InputStream.class), any(ObjectMetadata.class)))
      .thenReturn(por);
    String header = "ekonomi/2022/inrikes/1617";
    Meta result = null;
    try {
      result = imageRepository.save(ARTICLE_IMAGE, header, new CorrectMultiPart());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    MetaDTO expected = new MetaDTO(OffsetDateTime.now(), por.getContentMd5(),
      por.getETag(), por.getVersionId());
    assertEquals(expected.contentMd5(), result.getContentMd5());
    assertEquals(expected.eTag(), result.getETag());
    assertEquals(expected.versionId(), result.getVersionId());
  }
}