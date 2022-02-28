package se.epochtimes.backend.images.model;

public enum BucketName {
  ARTICLE_IMAGE("us-epochtimes-images");

  private final String bucketName;

  BucketName(String bucketName) {
    this.bucketName = bucketName;
  }

  public String getBucketName() {
    return bucketName;
  }
}
