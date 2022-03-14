package se.epochtimes.backend.images.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.controller.ImageController;
import se.epochtimes.backend.images.dto.FileDTO;
import se.epochtimes.backend.images.exception.*;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.File;
import se.epochtimes.backend.images.model.file.Meta;
import se.epochtimes.backend.images.model.multipart.BadIOMultiPart;
import se.epochtimes.backend.images.model.multipart.ContentMultiPart;
import se.epochtimes.backend.images.model.multipart.CorrectMultiPart;
import se.epochtimes.backend.images.model.multipart.EmptyMultiPart;
import se.epochtimes.backend.images.repository.FileRepository;
import se.epochtimes.backend.images.repository.ImageRepository;
import se.epochtimes.backend.images.repository.TextRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.entity.ContentType.*;
import static org.junit.jupiter.api.Assertions.*;
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
  private FileRepository mockedFileRepository;

  @InjectMocks
  private ImageService imageServiceTest;

  private String h;

  @BeforeEach
  void setUp() {
    h = ImageController.PREFIX + "1617";
  }

  @Test
  void getAllFilesUnsorted() {
    List<File> files = new ArrayList<>();
    files.add(new File("inrikes/2022/ekonomi/1617/swaggerimage.png",
      new Meta("lSOXNcoa5LdYv2q0ZJO9wg==",
        "95239735ca1ae4b758bf6ab46493bdc2",
        "6bZCa.Yn9xM05XHiXaMvZTopLdkeOBtQ")));
    files.add(new File("inrikes/2022/ekonomi/1617/mock.png",
      new Meta("RdoTMXFQ2Ahmug1P+Eutfw==",
        "45da13317150d80866ba0d4ff84bad7f",
        "8zVkx0eyjSKQP9Gd.dNBQUPkh1WjHHSO")));
    when(mockedFileRepository.findAll()).thenReturn(files);
    assertTrue(imageServiceTest.getAllUnsorted().size() > 0);
  }

  @Test
  void shouldThrowEmptyFileException() {
    assertThrows(EmptyFileException.class,
      () -> imageServiceTest.save(h, ARTICLE_IMAGE, new EmptyMultiPart()));
  }

  @Test
  void shouldThrowNotAnImageExceptionWithPlainText() {
    assertThrows(NotAnImageException.class,
      () -> imageServiceTest.save(h, ARTICLE_IMAGE, new ContentMultiPart(TEXT_PLAIN)));
  }

  @Test
  void shouldThrowNotAnImageExceptionWithHtml() {
    assertThrows(NotAnImageException.class,
      () -> imageServiceTest.save(h, ARTICLE_IMAGE, new ContentMultiPart(TEXT_HTML)));
  }

  @Test
  void shouldThrowAlreadyAddedException() {
    when(mockedFileRepository.existsByFilePath(any(String.class))).thenReturn(true);
    assertThrows(AlreadyAddedException.class,
      () -> imageServiceTest.save(h, ARTICLE_IMAGE, new CorrectMultiPart()));
  }

  @Test
  void shouldThrowArticleNotFoundException() {
    h = ImageController.PREFIX + "1616";
    when(mockedFileRepository.existsByFilePath(any(String.class))).thenReturn(false);
    when(mockedTextRepository.isArticleAvailable(any(String.class))).thenReturn(false);
    assertThrows(ArticleNotFoundException.class,
      () -> imageServiceTest.save(h, ARTICLE_IMAGE, new ContentMultiPart(IMAGE_JPEG)));
  }

  @Test
  void shouldThrowFileReadingException() throws IOException {
    when(mockedFileRepository.existsByFilePath(any(String.class))).thenReturn(false);
    when(mockedTextRepository.isArticleAvailable(any(String.class))).thenReturn(true);
    when(mockedImageRepository
      .save(any(BucketName.class), any(String.class), any(MultipartFile.class))
    ).thenThrow(new IOException());
    assertThrows(FileReadingException.class,
      () -> imageServiceTest.save(h, ARTICLE_IMAGE, new BadIOMultiPart()));
  }

  @Test
  void shouldReturnMetaDto() throws IOException {
    MultipartFile multiFile = new CorrectMultiPart();
    Meta meta = new Meta("A", "B", "C");
    File model = new File(h + multiFile.getOriginalFilename(), meta);
    when(mockedFileRepository.existsByFilePath(any(String.class))).thenReturn(false);
    when(mockedTextRepository.isArticleAvailable(any(String.class))).thenReturn(true);
    when(mockedImageRepository
      .save(any(BucketName.class), any(String.class), any(MultipartFile.class))
    ).thenReturn(model);
    when(mockedFileRepository.save(any(File.class))).thenReturn(model);
    FileDTO dto = imageServiceTest.save(h, ARTICLE_IMAGE, multiFile);
    assertEquals(model.getTime(), dto.getTime());
  }

  @Test
  void shouldDownloadAnImage() {
    assertNotNull(imageServiceTest.get("1617", "swaggerimage.png"));
  }

}
