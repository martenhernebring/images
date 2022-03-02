package se.epochtimes.backend.images.exception;

public class ArticleNotFoundException extends RuntimeException {
  public ArticleNotFoundException(String message) {
    super(message);
  }
}
