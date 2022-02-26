package se.epochtimes.backend.images.bucket;

public enum BucketName {
  ARTICLE_IMAGE("se-epochtimes-images");

  private final String bucketName;

  BucketName(String bucketName) {
    this.bucketName = bucketName;
  }

  public String getBucketName() {
    return bucketName;
  }
}
