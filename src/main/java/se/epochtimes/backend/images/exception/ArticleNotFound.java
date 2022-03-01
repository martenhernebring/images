package se.epochtimes.backend.images.exception;

public class ArticleNotFound extends RuntimeException {
  public ArticleNotFound(String message) {
    super(message);
  }
}
