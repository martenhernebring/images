package se.epochtimes.backend.images.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import se.epochtimes.backend.images.client.TextService;
import se.epochtimes.backend.images.model.HeaderComponent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextServiceIT {

  @Test
  void isArticleAvailable() {
    HeaderComponent hc = new HeaderComponent(
      "ekonomi", 2022, "inrikes", "1617"
    );
    TextService textService = new TextService();
    var mockWebServer = new MockWebServer();
    mockWebServer.enqueue(
      new MockResponse().setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    );
    String h = hc.vignette()  + "/" + hc.subYear() + "/" + hc.subject() + "/" + hc.articleId();
    assertTrue(textService.isArticleAvailable(h));
  }

  @Test
  void notAvailable() {
    HeaderComponent hc = new HeaderComponent(
      "ekonomi", 2022, "inrikes", "1616"
    );
    TextService textService = new TextService();
    var mockWebServer = new MockWebServer();
    mockWebServer.enqueue(
      new MockResponse().setResponseCode(404)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody("{\"error_code\": null, \"error_message\": null}")
    );
    String h = hc.vignette()  + "/" + hc.subYear() + "/" + hc.subject() + "/" + hc.articleId();
    assertFalse(textService.isArticleAvailable(h));
  }
}