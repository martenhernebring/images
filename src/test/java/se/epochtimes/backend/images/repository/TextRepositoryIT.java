package se.epochtimes.backend.images.repository;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.epochtimes.backend.images.config.TextConfiguration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.epochtimes.backend.images.controller.ImageController.PREFIX;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TextConfiguration.class)
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class TextRepositoryIT {

  @Autowired
  private TextConfiguration textConfiguration;

  private TextRepository textRepository;
  private MockWebServer mockWebServer;

  @BeforeEach
  void setUp() {
    textRepository = new TextClient(textConfiguration.client());
    mockWebServer = new MockWebServer();
  }

  @Test
  void isArticleAvailable() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    assertTrue(textRepository.isArticleAvailable(PREFIX + "1617"));
  }

  @Test
  void notAvailable() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(404)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody("{\"error_code\": null, \"error_message\": null}"));
    assertFalse(textRepository.isArticleAvailable(PREFIX + "1616"));
  }
}