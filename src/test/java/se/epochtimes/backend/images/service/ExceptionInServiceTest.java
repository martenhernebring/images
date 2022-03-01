package se.epochtimes.backend.images.service;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import se.epochtimes.backend.images.exception.EmptyFileException;
import se.epochtimes.backend.images.exception.FileReadingException;
import se.epochtimes.backend.images.exception.NotAnImageException;
import se.epochtimes.backend.images.model.HeaderComponent;
import se.epochtimes.backend.images.model.Subject;
import se.epochtimes.backend.images.service.multipart.BadIOMultiPart;
import se.epochtimes.backend.images.service.multipart.ContentMultiPart;
import se.epochtimes.backend.images.service.multipart.EmptyMultiPart;

import static org.apache.http.entity.ContentType.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static se.epochtimes.backend.images.model.BucketName.ARTICLE_IMAGE;

@ExtendWith(MockitoExtension.class)
public class ExceptionInServiceTest {

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
  void shouldThrowEmptyFileException() {
    assertThrows(EmptyFileException.class,
      () -> fileServiceTest.save(hc, ARTICLE_IMAGE, new EmptyMultiPart()));
  }

  @Test
  void shouldThrowNotAnImageExceptionWithPlainText() {
    assertThrows(NotAnImageException.class,
      () -> fileServiceTest.save(hc, ARTICLE_IMAGE, new ContentMultiPart(TEXT_PLAIN)));
  }

  @Test
  void shouldThrowNotAnImageExceptionWithHtml() {
    assertThrows(NotAnImageException.class,
      () -> fileServiceTest.save(hc, ARTICLE_IMAGE, new ContentMultiPart(TEXT_HTML)));
  }

  @Test
  void shouldThrowArticleNotFoundException() {
    hc = new HeaderComponent(
      Subject.EKONOMI, 2022, "Inrikes", "1616"
    );
    assertThrows(WebClientResponseException.NotFound.class,
      () -> fileServiceTest.save(hc, ARTICLE_IMAGE, new ContentMultiPart(IMAGE_JPEG)));
  }

  @Test
  void shouldThrowStorageFailureException() {
    assertThrows(FileReadingException.class,
      () -> fileServiceTest.save(hc, ARTICLE_IMAGE, new BadIOMultiPart()));
  }

}
