package se.epochtimes.backend.images.repository.text;

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
    int result;
    try {
      result = textClient
        .get()
        .uri(header)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .toBodilessEntity()
        .block()
        .getStatusCode()
        .value();
    } catch (WebClientResponseException ex) {
      return false;
    }
    return true;
  }
}
