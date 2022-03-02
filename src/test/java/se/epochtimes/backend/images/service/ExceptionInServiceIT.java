package se.epochtimes.backend.images.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import se.epochtimes.backend.images.dto.MetaDTO;
import se.epochtimes.backend.images.exception.FileReadingException;
import se.epochtimes.backend.images.model.HeaderComponent;
import se.epochtimes.backend.images.model.Subject;
import se.epochtimes.backend.images.service.multipart.BadIOMultiPart;
import se.epochtimes.backend.images.service.multipart.ContentMultiPart;
import se.epochtimes.backend.images.service.multipart.CorrectMultiPart;

import java.io.InputStream;
import java.util.Objects;

import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static se.epochtimes.backend.images.model.BucketName.ARTICLE_IMAGE;

@ExtendWith(MockitoExtension.class)
@Disabled
public class ExceptionInServiceIT {

  @Mock
  private AmazonS3 s3;

  @InjectMocks
  private FileService fileServiceTest;

  private HeaderComponent hc;

  @BeforeEach
  void setUp() {
    hc = new HeaderComponent(
      Subject.EKONOMI, 2022, "Inrikes", "1617"
    );
  }

  @Test
  void shouldGetValueOk() {
    HeaderComponent hc = new HeaderComponent(
      Subject.EKONOMI, 2022, "Inrikes", "1617"
    );
    String baseUrl = "http://localhost:8181/v1/articles/";
    String header = hc.vignette().toLowerCase()  + "/" + hc.subYear() + "/" +
      hc.subject().getPrint().toLowerCase() + "/" + hc.articleId();
    WebClient client = WebClient.builder().baseUrl(baseUrl).build();
    WebClient.ResponseSpec response = client
      .get()
      .uri(header)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve();
    assertEquals(200, Objects.requireNonNull(response.toBodilessEntity().block()).getStatusCode().value());
  }

  @Test
  void shouldThrowArticleNotFoundException() {
    hc = new HeaderComponent(
      Subject.EKONOMI, 2022, "Inrikes", "1616"
    );
    assertThrows(WebClientResponseException.class,
      () -> fileServiceTest.save(hc, ARTICLE_IMAGE, new ContentMultiPart(IMAGE_JPEG)));
  }

  @Test
  void shouldThrowStorageFailureException() {
    assertThrows(FileReadingException.class,
      () -> fileServiceTest.save(hc, ARTICLE_IMAGE, new BadIOMultiPart()));
  }

  @Test
  void shouldNotThrowWhenSaving() {
    PutObjectResult por = new PutObjectResult();
    por.setContentMd5("sWSbvU0leS0QWOzgB5xIyw==");
    por.setETag("b1649bbd4d25792d1058ece0079c48cb");
    por.setVersionId("cPXs4Kq0FQhbnSl0IGNXMEPA4NLRIfGj");
    MetaDTO expected = new MetaDTO(por.getContentMd5(),
      por.getETag(), por.getVersionId());
    when(s3.putObject(any(String.class), any(String.class),
      any(InputStream.class), any(ObjectMetadata.class))
    ).thenReturn(por);
    var result = fileServiceTest.save(hc, ARTICLE_IMAGE, new CorrectMultiPart());
    assertEquals(expected, result);
  }
}
