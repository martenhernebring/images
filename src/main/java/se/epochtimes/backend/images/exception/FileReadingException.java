package se.epochtimes.backend.images.exception;

import java.io.IOException;

public class FileReadingException extends RuntimeException {
  public FileReadingException(String message, IOException e) {
    super(message, e);
  }
}
