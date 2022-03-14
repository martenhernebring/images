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
import se.epochtimes.backend.images.model.Meta;
import se.epochtimes.backend.images.multipart.CorrectMultiPart;
import se.epochtimes.backend.images.repository.image.ImageStorage;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageRepositoryTest {

  @Mock
  AmazonS3 amazonS3;

  @InjectMocks
  ImageStorage imageRepository;
  
  private static final String HEADER = "ekonomi/2022/inrikes/1617";
  private static final String FILE_PATH = HEADER + "/mock.png";

  @Test
  void shouldThrowStorageFailureExceptionWhenDownloading() {
    when(amazonS3.getObject(any(String.class), any(String.class)))
      .thenThrow(new AmazonServiceException("Test"));
    assertThrows(StorageFailureException.class,
      () -> imageRepository.download(FILE_PATH));
  }

  @Test
  void shouldThrowStorageFailureExceptionWhenSaving() {
    when(amazonS3.putObject(any(String.class),
      any(String.class), any(InputStream.class), any(ObjectMetadata.class)))
      .thenThrow(new AmazonServiceException("Test"));
    assertThrows(StorageFailureException.class,
      () -> imageRepository.save(HEADER, new CorrectMultiPart()));
  }

  @Test
  void shouldNotThrowWhenSavingCorrectly() {
    PutObjectResult por = setupPutObjectResult();
    Meta meta = setupMeta(por);
    MultipartFile file = new CorrectMultiPart();
    File fileData = setupFileData(meta, file);
    when(amazonS3.putObject(any(String.class), any(String.class), any(InputStream.class), any(ObjectMetadata.class)))
      .thenReturn(por);

    File result = null;
    try {
      result = imageRepository.save(HEADER, file);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    assertEquals(fileData.getFilePath(), result.getFilePath());
    FileDTO expected = new FileDTO(OffsetDateTime.now(), result.getFilePath(), result.getMeta());
    assertEquals(expected.getMeta().getContentMd5(), result.getMeta().getContentMd5());
    assertEquals(expected.getMeta().getETag(), result.getMeta().getETag());
    assertEquals(expected.getMeta().getVersionId(), result.getMeta().getVersionId());
  }

  private Meta setupMeta(PutObjectResult por) {
    Meta meta = new Meta();
    meta.setContentMd5(por.getContentMd5());
    meta.setETag(por.getETag());
    meta.setVersionId(por.getVersionId());
    return meta;
  }

  private File setupFileData(Meta meta, MultipartFile multipartFile) {
    File file = new File();
    file.setId(1234L);
    assertEquals(1234L, file.getId());
    file.setTime();
    file.setMeta(meta);
    file.setFilePath(HEADER + "/" + multipartFile.getOriginalFilename());
    return file;
  }

  private PutObjectResult setupPutObjectResult() {
    PutObjectResult por = new PutObjectResult();
    por.setContentMd5("sWSbvU0leS0QWOzgB5xIyw==");
    por.setETag("b1649bbd4d25792d1058ece0079c48cb");
    por.setVersionId("cPXs4Kq0FQhbnSl0IGNXMEPA4NLRIfGj");
    return por;
  }
}