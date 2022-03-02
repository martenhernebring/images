package se.epochtimes.backend.images.repository;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Repository("textRepository")
public class TextClient implements TextRepository {

  private final WebClient textClient;

  public TextClient(WebClient textClient) {
    this.textClient = textClient;
  }

  @Override
  public boolean isArticleAvailable(String header) {
    try {
      var response = textClient
        .get()
        .uri(header)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .toBodilessEntity()
        .block()
        .getStatusCode()
        .value();
      return true;
    } catch (WebClientResponseException ex) {
      return false;
    }
  }
}
