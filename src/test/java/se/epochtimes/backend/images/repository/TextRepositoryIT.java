package se.epochtimes.backend.images.repository;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.epochtimes.backend.images.config.TextConfiguration;
import se.epochtimes.backend.images.controller.ImageController;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TextConfiguration.class)
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class TextRepositoryIT {

  @Autowired
  private TextConfiguration textConfiguration;

  @Test
  void isArticleAvailable() {
    TextClient textRepository = new TextClient(textConfiguration.client());
    var mockWebServer = new MockWebServer();
    mockWebServer.enqueue(
      new MockResponse().setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    );
    assertTrue(textRepository.isArticleAvailable(ImageController.PREFIX + "1617"));
  }

  @Test
  void notAvailable() {
    TextRepository textRepository = new TextClient(textConfiguration.client());
    var mockWebServer = new MockWebServer();
    mockWebServer.enqueue(
      new MockResponse().setResponseCode(404)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody("{\"error_code\": null, \"error_message\": null}")
    );
    assertFalse(textRepository.isArticleAvailable(ImageController.PREFIX + "1616"));
  }
}