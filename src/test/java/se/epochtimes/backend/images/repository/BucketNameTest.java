package se.epochtimes.backend.images.repository;

import org.junit.jupiter.api.Test;
import se.epochtimes.backend.images.repository.image.BucketName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BucketNameTest {
  @Test
  void articleImageExists() {
    assertNotNull(BucketName.ARTICLE_IMAGE);
  }

  @Test
  void bucketNameIsCorrect() {
    assertEquals("us-epochtimes-images", BucketName.ARTICLE_IMAGE.getBucketName());
  }
}
