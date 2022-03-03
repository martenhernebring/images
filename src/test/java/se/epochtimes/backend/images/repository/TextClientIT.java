package se.epochtimes.backend.images.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.epochtimes.backend.images.config.TextConfiguration;
import se.epochtimes.backend.images.controller.ImageController;

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
    String header = ImageController.PREFIX + "1617";
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