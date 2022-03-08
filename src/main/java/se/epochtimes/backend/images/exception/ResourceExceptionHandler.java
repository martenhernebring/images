package se.epochtimes.backend.images.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResourceExceptionHandler {
  @ExceptionHandler(AlreadyAddedException.class)
  public ResponseEntity<StandardError> alreadyAdded(AlreadyAddedException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    StandardError err = new StandardError(
      status.value(), "AlreadyAdded", e.getMessage()
    );
    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(ArticleNotFoundException.class)
  public ResponseEntity<StandardError> articleNotFound(ArticleNotFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    StandardError err = new StandardError(
      status.value(), "ArticleNotFound", e.getMessage()
    );
    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(EmptyFileException.class)
  public ResponseEntity<StandardError> emptyFile(EmptyFileException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    StandardError err = new StandardError(
      status.value(), "EmptyFile", e.getMessage()
    );
    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(NotAnImageException.class)
  public ResponseEntity<StandardError> notAnImage(NotAnImageException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    StandardError err = new StandardError(
      status.value(), "NotAnImage", e.getMessage()
    );
    return ResponseEntity.status(status).body(err);
  }
}
