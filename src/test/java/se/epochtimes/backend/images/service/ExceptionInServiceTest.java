package se.epochtimes.backend.images.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import se.epochtimes.backend.images.client.TextService;
import se.epochtimes.backend.images.dto.MetaDTO;
import se.epochtimes.backend.images.exception.*;
import se.epochtimes.backend.images.model.HeaderComponent;
import se.epochtimes.backend.images.model.Image;
import se.epochtimes.backend.images.repository.ImageRepository;
import se.epochtimes.backend.images.service.multipart.BadIOMultiPart;
import se.epochtimes.backend.images.service.multipart.ContentMultiPart;
import se.epochtimes.backend.images.service.multipart.CorrectMultiPart;
import se.epochtimes.backend.images.service.multipart.EmptyMultiPart;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Objects;

import static org.apache.http.entity.ContentType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static se.epochtimes.backend.images.model.BucketName.ARTICLE_IMAGE;

@ExtendWith(MockitoExtension.class)
public class ExceptionInServiceTest {

  @Mock
  private AmazonS3 mockedS3;

  @Mock
  private TextService mockedTextService;

  @Mock
  private ImageRepository mockedRepository;

  @InjectMocks
  private ImageService imageServiceTest;

  private HeaderComponent hc;

  @BeforeEach
  void setUp() {
    hc = new HeaderComponent(
      "ekonomi", 2022, "inrikes", "1617"
    );
  }


  @Test
  void shouldThrowEmptyFileException() {
    assertThrows(EmptyFileException.class,
      () -> imageServiceTest.save(hc, ARTICLE_IMAGE, new EmptyMultiPart()));
  }

  @Test
  void shouldThrowNotAnImageExceptionWithPlainText() {
    assertThrows(NotAnImageException.class,
      () -> imageServiceTest.save(hc, ARTICLE_IMAGE, new ContentMultiPart(TEXT_PLAIN)));
  }

  @Test
  void shouldThrowNotAnImageExceptionWithHtml() {
    assertThrows(NotAnImageException.class,
      () -> imageServiceTest.save(hc, ARTICLE_IMAGE, new ContentMultiPart(TEXT_HTML)));
  }

  @Test
  void shouldGetValueOk() {
    HeaderComponent hc = new HeaderComponent(
      "ekonomi", 2022, "inrikes", "1617"
    );
    String baseUrl = "http://localhost:8181/v1/articles/";
    String header = hc.vignette().toLowerCase()  + "/" + hc.subYear() + "/" +
      hc.subject() + "/" + hc.articleId();
    WebClient client = WebClient.builder().baseUrl(baseUrl).build();
    WebClient.ResponseSpec response = client
      .get()
      .uri(header)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve();
    assertEquals(200, Objects.requireNonNull(
      response.toBodilessEntity().block()).getStatusCode().value()
    );
  }

  @Test
  void shouldThrowArticleNotFoundException() {
    hc = new HeaderComponent(
      "ekonomi", 2022, "inrikes", "1616"
    );
    assertThrows(ArticleNotFoundException.class,
      () -> imageServiceTest.save(hc, ARTICLE_IMAGE, new ContentMultiPart(IMAGE_JPEG)));
  }

  @Test
  void shouldThrowFileReadingException() {
    when(mockedTextService.isArticleAvailable(any(String.class))).thenReturn(true);
    assertThrows(FileReadingException.class,
      () -> imageServiceTest.save(hc, ARTICLE_IMAGE, new BadIOMultiPart()));
  }

  @Test
  void shouldThrowStorageFailureException() {
    when(mockedTextService.isArticleAvailable(any(String.class))).thenReturn(true);
    when(mockedS3.putObject(any(String.class), any(String.class),
      any(InputStream.class), any(ObjectMetadata.class)))
      .thenThrow(new AmazonServiceException("Test"));
    assertThrows(StorageFailureException.class,
      () -> imageServiceTest.save(hc, ARTICLE_IMAGE, new CorrectMultiPart()));
  }

  @Test
  void shouldNotThrowWhenSaving() {
    PutObjectResult por = new PutObjectResult();
    por.setContentMd5("sWSbvU0leS0QWOzgB5xIyw==");
    por.setETag("b1649bbd4d25792d1058ece0079c48cb");
    por.setVersionId("cPXs4Kq0FQhbnSl0IGNXMEPA4NLRIfGj");
    when(mockedTextService.isArticleAvailable(any(String.class))).thenReturn(true);
    when(mockedS3.putObject(any(String.class), any(String.class),
      any(InputStream.class), any(ObjectMetadata.class))
    ).thenReturn(por);
    Image i = new Image();
    i.setId(1234L);
    i.setTime();
    i.setContentMd5(por.getContentMd5());
    i.setETag(por.getETag());
    i.setVersionId(por.getVersionId());
    assertEquals(1234L, i.getId());
    when(mockedRepository.save(any(Image.class))).thenReturn(i);
    var result = imageServiceTest.save(hc, ARTICLE_IMAGE, new CorrectMultiPart());
    MetaDTO expected = new MetaDTO(OffsetDateTime.now(), por.getContentMd5(),
      por.getETag(), por.getVersionId());
    assertEquals(expected.contentMd5(), result.contentMd5());
    assertEquals(expected.eTag(), result.eTag());
    assertEquals(expected.versionId(), result.versionId());
  }

}
