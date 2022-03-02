package se.epochtimes.backend.images.exception;

public class FileReadingException extends RuntimeException {
  public FileReadingException(String message, Exception e) {
    super(message, e);
  }
}
