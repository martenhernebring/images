package se.epochtimes.backend.images.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.Serializable;

@ControllerAdvice
public class StandardError implements Serializable {

  private Integer status;
  private String error;
  private String message;

  public StandardError() {}

  public StandardError(Integer status, String error, String message) {
    super();
    this.status = status;
    this.error = error;
    this.message = message;
  }

  public String getError() {
    return error;
  }

  public Integer getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
