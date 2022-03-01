package se.epochtimes.backend.images.exception;

public class NotAnImageException extends RuntimeException {
  public NotAnImageException(String message) {
    super(message);
  }
}
