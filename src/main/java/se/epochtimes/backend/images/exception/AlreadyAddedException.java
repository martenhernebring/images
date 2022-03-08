package se.epochtimes.backend.images.exception;

public class AlreadyAddedException extends RuntimeException {
  public AlreadyAddedException(String message) {
    super(message);
  }
}
