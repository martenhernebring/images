package se.epochtimes.backend.images.exception;

public class EmptyFileException extends RuntimeException{
  public EmptyFileException(String message) {
    super(message);
  }
}
