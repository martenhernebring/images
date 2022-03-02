package se.epochtimes.backend.images.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.epochtimes.backend.images.config.TextConfiguration;
import se.epochtimes.backend.images.model.HeaderComponent;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TextConfiguration.class)
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class TextClientIT {

  @Autowired
  private TextConfiguration textConfiguration;

  @Test
  void shouldGetValueOk() {
    HeaderComponent hc = new HeaderComponent(
      "ekonomi", 2022, "inrikes", "1617"
    );
    String baseUrl = "http://localhost:8181/v1/articles/";
    String header = hc.vignette().toLowerCase()  + "/" + hc.subYear() + "/" +
      hc.subject() + "/" + hc.articleId();
    var response = textConfiguration
      .client()
      .get()
      .uri(header)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve();
    assertEquals(200, Objects.requireNonNull(
      response.toBodilessEntity().block()).getStatusCode().value()
    );
  }
}