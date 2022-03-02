package se.epochtimes.backend.images.repository;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.epochtimes.backend.images.config.TextConfiguration;
import se.epochtimes.backend.images.model.HeaderComponent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TextConfiguration.class)
class TextRepositoryIT {

  @Autowired
  private TextConfiguration textConfiguration;

  @Test
  void isArticleAvailable() {
    HeaderComponent hc = new HeaderComponent(
      "ekonomi", 2022, "inrikes", "1617"
    );
    TextClient textRepository = new TextClient(textConfiguration.client());
    var mockWebServer = new MockWebServer();
    mockWebServer.enqueue(
      new MockResponse().setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    );
    String h = hc.vignette()  + "/" + hc.subYear() + "/" + hc.subject() + "/" + hc.articleId();
    assertTrue(textRepository.isArticleAvailable(h));
  }

  @Test
  void notAvailable() {
    HeaderComponent hc = new HeaderComponent(
      "ekonomi", 2022, "inrikes", "1616"
    );
    TextRepository textRepository = new TextClient(textConfiguration.client());
    var mockWebServer = new MockWebServer();
    mockWebServer.enqueue(
      new MockResponse().setResponseCode(404)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody("{\"error_code\": null, \"error_message\": null}")
    );
    String h = hc.vignette()  + "/" + hc.subYear() + "/" + hc.subject() + "/" + hc.articleId();
    assertFalse(textRepository.isArticleAvailable(h));
  }
}