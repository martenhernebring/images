package se.epochtimes.backend.images.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AmazonConfig.class)
public class AmazonConfigIT {

  @Autowired
  private AmazonConfig amazonConfig;

  @Test
  void s3hasRegionIreland() {
    assertEquals("eu-west-1", amazonConfig.s3().getRegionName());
  }

  @Test
  void s3hasOnlyOneBucketWithCorrectName() {
    var buckets = amazonConfig.s3().listBuckets();
    assertEquals(1, buckets.size());
    assertEquals("eu-epochtimes-images", buckets.get(0).getName());
  }
}
