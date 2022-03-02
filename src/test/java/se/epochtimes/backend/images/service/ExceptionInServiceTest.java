package se.epochtimes.backend.images.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.epochtimes.backend.images.exception.EmptyFileException;
import se.epochtimes.backend.images.exception.NotAnImageException;
import se.epochtimes.backend.images.model.HeaderComponent;
import se.epochtimes.backend.images.model.Subject;
import se.epochtimes.backend.images.service.multipart.ContentMultiPart;
import se.epochtimes.backend.images.service.multipart.EmptyMultiPart;

import static org.apache.http.entity.ContentType.TEXT_HTML;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static se.epochtimes.backend.images.model.BucketName.ARTICLE_IMAGE;

@ExtendWith(MockitoExtension.class)
public class ExceptionInServiceTest {

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

}
