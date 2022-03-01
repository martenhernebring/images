package se.epochtimes.backend.images.exception;

import com.amazonaws.AmazonServiceException;

public class StorageFailureException extends RuntimeException {
  public StorageFailureException(String message, AmazonServiceException e) {
    super(message, e);
  }
}
