package se.epochtimes.backend.images.controller;

import com.amazonaws.util.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;
import se.epochtimes.backend.images.service.FileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan(basePackages = "se.epochtimes.backend.images.controller")
@WebMvcTest(FileController.class)
public class FileControllerTest {

  @MockBean
  private FileService fileService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMultipartFile file;

  @BeforeEach
  void setUp() {
    File initialFile = null;
    try {
      initialFile = ResourceUtils.getFile("classpath:static/images/20220227_143037.jpg");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail();
    }
    file = null;
    try {
      file
        = new MockMultipartFile(
        "file",
        "20220227_143037.jpg",
        MediaType.MULTIPART_FORM_DATA_VALUE,
        IOUtils.toByteArray(new FileInputStream(initialFile))
      );
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  void postImage() throws Exception {
    MockMvc mockMvc
      = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    mockMvc.perform(multipart("/v1/images/inrikes/2022/ekonomi/1617").file(file))
      .andExpect(status().isOk());
  }
}
