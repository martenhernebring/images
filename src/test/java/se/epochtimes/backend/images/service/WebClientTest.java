package se.epochtimes.backend.images.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import se.epochtimes.backend.images.model.HeaderComponent;
import se.epochtimes.backend.images.model.Subject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class WebClientTest {
  @Test
  void shouldGetValueOk() {
    HeaderComponent hc = new HeaderComponent(
      Subject.EKONOMI, 2022, "Inrikes", "1617"
    );
    String baseUrl = "http://localhost:8181/v1/articles/";
    String header = hc.vignette().toLowerCase()  + "/" + hc.subYear() + "/" +
      hc.subject().getPrint().toLowerCase() + "/" + hc.articleId();
    WebClient client = WebClient.builder().baseUrl(baseUrl).build();
    WebClient.ResponseSpec response = client
      .get()
      .uri(header)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve();
    assertEquals(200, response.toBodilessEntity().block().getStatusCode().value());
    }
}
