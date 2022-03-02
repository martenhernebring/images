package se.epochtimes.backend.images.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component("textService")
public class TextService {
  private static final String BASE_URL = "http://localhost:8181/v1/articles/";

  public boolean isArticleAvailable(String header) {
    try{
      WebClient client = WebClient.builder().baseUrl(BASE_URL).build();
      var response = client
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
