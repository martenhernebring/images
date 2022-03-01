package se.epochtimes.backend.images.service;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import se.epochtimes.backend.images.exception.EmptyFileException;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.HeaderComponent;
import se.epochtimes.backend.images.model.Subject;
import se.epochtimes.backend.images.repository.FileRepository;
import se.epochtimes.backend.images.service.multipart.EmptyMultiPart;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ExceptionInServiceTest {

  @Mock
  private AmazonS3 s3;

  @InjectMocks
  private FileService fileServiceTest;


  @Test
  void shouldThrowEmptyFileException() {
    final HeaderComponent hc = new HeaderComponent(
      Subject.EKONOMI, 2022, "Inrikes", "1617"
    );
    assertThrows(EmptyFileException.class,
      () -> fileServiceTest.save(hc, BucketName.ARTICLE_IMAGE, new EmptyMultiPart()));
  }

}
