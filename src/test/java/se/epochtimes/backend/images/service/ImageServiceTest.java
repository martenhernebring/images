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
import se.epochtimes.backend.images.model.File;
import se.epochtimes.backend.images.model.Meta;
import se.epochtimes.backend.images.multipart.BadIOMultiPart;
import se.epochtimes.backend.images.multipart.ContentMultiPart;
import se.epochtimes.backend.images.multipart.CorrectMultiPart;
import se.epochtimes.backend.images.multipart.EmptyMultiPart;
import se.epochtimes.backend.images.repository.file.FileRepository;
import se.epochtimes.backend.images.repository.image.ImageRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.entity.ContentType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

  @Mock
  private ImageRepository mockedImageRepository;

  @Mock
  private FileRepository mockedFileRepository;

  @InjectMocks
  private ImageService imageServiceTest;

  private String h;

  @BeforeEach
  void setUp() {
    h = "/INRIKES/2022/1617/ekonomi/swaggerimage.png";
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
      () -> imageServiceTest.save(h, new EmptyMultiPart()));
  }

  @Test
  void shouldThrowNotAnImageExceptionWithPlainText() {
    assertThrows(NotAnImageException.class,
      () -> imageServiceTest.save(h, new ContentMultiPart(TEXT_PLAIN)));
  }

  @Test
  void shouldThrowNotAnImageExceptionWithHtml() {
    assertThrows(NotAnImageException.class,
      () -> imageServiceTest.save(h, new ContentMultiPart(TEXT_HTML)));
  }

  @Test
  void shouldThrowAlreadyAddedException() {
    when(mockedFileRepository.existsByFilePath(any(String.class))).thenReturn(true);
    assertThrows(AlreadyAddedException.class,
      () -> imageServiceTest.save(h, new CorrectMultiPart()));
  }

  @Test
  void shouldThrowFileReadingExceptionWhenSaving() throws IOException {
    when(mockedFileRepository.existsByFilePath(any(String.class))).thenReturn(false);
    when(mockedImageRepository
      .save(any(String.class), any(MultipartFile.class))
    ).thenThrow(new IOException());
    assertThrows(FileReadingException.class,
      () -> imageServiceTest.save(h, new BadIOMultiPart()));
  }

  @Test
  void shouldReturnMetaDto() throws IOException {
    MultipartFile multiFile = new CorrectMultiPart();
    Meta meta = new Meta("A", "B", "C");
    File model = new File(h + multiFile.getOriginalFilename(), meta);
    when(mockedFileRepository.existsByFilePath(any(String.class))).thenReturn(false);
    when(mockedImageRepository
      .save(any(String.class), any(MultipartFile.class))
    ).thenReturn(model);
    when(mockedFileRepository.save(any(File.class))).thenReturn(model);
    FileDTO dto = imageServiceTest.save(h, multiFile);
    assertEquals(model.getTime(), dto.getTime());
  }

  @Test
  void shouldThrowFileReadingExceptionWhenDownloading() throws IOException {
    when(mockedImageRepository.download(any(String.class))).thenThrow(new IOException());
    assertThrows(FileReadingException.class,
      () -> imageServiceTest.get(h, "swaggerimage.png"));
  }

  @Test
  void shouldDownloadAnImage() throws IOException {
    when(mockedImageRepository.download(any(String.class))).thenReturn(new byte[1]);
    var bytes = imageServiceTest.get(h, "swaggerimage.png");
    assertTrue(bytes.length > 0);
  }

}
