package se.epochtimes.backend.images.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.dto.MetaDTO;
import se.epochtimes.backend.images.exception.ArticleNotFoundException;
import se.epochtimes.backend.images.exception.EmptyFileException;
import se.epochtimes.backend.images.exception.FileReadingException;
import se.epochtimes.backend.images.exception.NotAnImageException;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.HeaderComponent;
import se.epochtimes.backend.images.model.Meta;
import se.epochtimes.backend.images.repository.ImageRepository;
import se.epochtimes.backend.images.repository.MetaRepository;
import se.epochtimes.backend.images.repository.TextRepository;
import se.epochtimes.backend.images.service.multipart.BadIOMultiPart;
import se.epochtimes.backend.images.service.multipart.ContentMultiPart;
import se.epochtimes.backend.images.service.multipart.CorrectMultiPart;
import se.epochtimes.backend.images.service.multipart.EmptyMultiPart;

import java.io.IOException;

import static org.apache.http.entity.ContentType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static se.epochtimes.backend.images.model.BucketName.ARTICLE_IMAGE;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

  @Mock
  private ImageRepository mockedImageRepository;

  @Mock
  private TextRepository mockedTextRepository;

  @Mock
  private MetaRepository mockedMetaRepository;

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
  void shouldThrowArticleNotFoundException() {
    hc = new HeaderComponent(
      "ekonomi", 2022, "inrikes", "1616"
    );
    when(mockedTextRepository.isArticleAvailable(any(String.class)))
      .thenReturn(false);
    assertThrows(ArticleNotFoundException.class,
      () -> imageServiceTest.save(hc, ARTICLE_IMAGE, new ContentMultiPart(IMAGE_JPEG)));
  }

  @Test
  void shouldThrowFileReadingException() throws IOException {
    when(mockedTextRepository.isArticleAvailable(any(String.class))).thenReturn(true);
    when(mockedImageRepository.save(any(BucketName.class), any(String.class), any(MultipartFile.class))).thenThrow(new IOException());
    assertThrows(FileReadingException.class,
      () -> imageServiceTest.save(hc, ARTICLE_IMAGE, new BadIOMultiPart()));
  }

  @Test
  void shouldReturnMetaDto() throws IOException {
    Meta model = new Meta("A", "B", "C");
    when(mockedTextRepository.isArticleAvailable(any(String.class)))
      .thenReturn(true);
    when(mockedImageRepository
      .save(any(BucketName.class), any(String.class), any(MultipartFile.class))
    ).thenReturn(model);
    when(mockedMetaRepository.save(any(Meta.class))).thenReturn(model);
    MetaDTO dto = imageServiceTest.save(hc, ARTICLE_IMAGE, new CorrectMultiPart());
    assertEquals(model.getTime(), dto.time());
  }

}
