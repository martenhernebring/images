package se.epochtimes.backend.images.controller;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.dto.FileDTO;
import se.epochtimes.backend.images.exception.AlreadyAddedException;
import se.epochtimes.backend.images.exception.ArticleNotFoundException;
import se.epochtimes.backend.images.exception.EmptyFileException;
import se.epochtimes.backend.images.exception.NotAnImageException;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.file.Meta;
import se.epochtimes.backend.images.service.ImageService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ComponentScan(basePackages = "se.epochtimes.backend.images.controller")
@WebMvcTest(ImageController.class)
public class ImageControllerTest {

  @MockBean
  private ImageService mockedService;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private ObjectMapper objectMapper;

  private String baseUrl;
  private String articleUrl;
  private MockMultipartFile mockedMultiFile = null;
  private FileDTO dto;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    baseUrl = "/v1/images";
    articleUrl = baseUrl + "/inrikes/2022/ekonomi/1617";
    File f = null;
    try {
      f = ResourceUtils.getFile("classpath:static/images/20220227_143037.jpg");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail();
    }
    try {
      this.mockedMultiFile = new MockMultipartFile(
        "file",
        "20220227_143037.jpg",
        MediaType.MULTIPART_FORM_DATA_VALUE,
        IOUtils.toByteArray(new FileInputStream(f))
      );
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    String filePath = articleUrl + "/" + mockedMultiFile.getOriginalFilename();
    Meta meta = new Meta("sWSbvU0leS0QWOzgB5xIyw==",
      "b1649bbd4d25792d1058ece0079c48cb", "cPXs4Kq0FQhbnSl0IGNXMEPA4NLRIfGj");
    dto = new FileDTO(OffsetDateTime.now(), filePath, meta);
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void getAllUnsorted() throws Exception {
    var dtos = List.of(dto);
    when(mockedService.getAllUnsorted()).thenReturn(dtos);
    String actualResponseJson =  mockMvc.perform(get(baseUrl))
      .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
      .andReturn().getResponse().getContentAsString();
    String expectedResultJson = objectMapper.writeValueAsString(dtos);
    assertEquals(expectedResultJson, actualResponseJson);
  }

  @Test
  void postImage() throws Exception {
    when(mockedService.
      save(any(String.class), any(BucketName.class), any(MultipartFile.class))
    ).thenReturn(dto);
    MvcResult mvcResult = mockMvc
      .perform(multipart(articleUrl).file(mockedMultiFile))
      .andExpect(status().isOk())
      .andReturn();

    String actualResponseJson = mvcResult.getResponse().getContentAsString();
    String expectedResultJson = objectMapper.writeValueAsString(dto);
    assertEquals(expectedResultJson, actualResponseJson);
  }

  @Test
  void postImageShouldReturnConflictWhenAlreadyAdded() throws Exception {
    when(mockedService.
      save(any(String.class), any(BucketName.class), any(MultipartFile.class))
    ).thenThrow(new AlreadyAddedException("Test"));
    mockMvc
      .perform(multipart(articleUrl).file(mockedMultiFile))
      .andExpect(status().isConflict())
      .andReturn();
  }

  @Test
  void postImageShouldReturnArticleNotFoundWhenNotAdded() throws Exception {
    when(mockedService.
      save(any(String.class), any(BucketName.class), any(MultipartFile.class))
    ).thenThrow(new ArticleNotFoundException("Test"));
    mockMvc
      .perform(multipart(articleUrl).file(mockedMultiFile))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void postImageShouldReturnEmptyFileWhenFileWasEmpty() throws Exception {
    when(mockedService.
      save(any(String.class), any(BucketName.class), any(MultipartFile.class))
    ).thenThrow(new EmptyFileException("Test"));
    String aRJ = mockMvc
      .perform(multipart(articleUrl).file(mockedMultiFile))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse()
      .getContentAsString();
    String err = "\"error\":\"";
    String msg = "\",\"message\":\"";
    assertEquals("EmptyFile",
      aRJ.substring(aRJ.indexOf(err) + err.length(), aRJ.indexOf(msg)));
  }

  @Test
  void postImageShouldReturnNotAnImageWhenNotAnImage() throws Exception {
    when(mockedService.
      save(any(String.class), any(BucketName.class), any(MultipartFile.class))
    ).thenThrow(new NotAnImageException("Test"));
    String aRJ = mockMvc
      .perform(multipart(articleUrl).file(mockedMultiFile))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse()
      .getContentAsString();
    String err = "\"error\":\"";
    String msg = "\",\"message\":\"";
    assertEquals("NotAnImage",
      aRJ.substring(aRJ.indexOf(err) + err.length(), aRJ.indexOf(msg)));
  }
}
