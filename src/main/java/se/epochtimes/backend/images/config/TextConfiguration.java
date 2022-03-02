package se.epochtimes.backend.images.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TextConfiguration {

  private static final String BASE_URL = "http://localhost:8181/v1/articles/";

  @Bean
  public WebClient client() {
    return WebClient.builder().baseUrl(BASE_URL).build();
  }
}
