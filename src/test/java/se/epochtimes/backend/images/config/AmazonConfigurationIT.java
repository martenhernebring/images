package se.epochtimes.backend.images.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AmazonConfiguration.class)
public class AmazonConfigurationIT {

  @Autowired
  private AmazonConfiguration amazonConfiguration;

  @Test
  void s3hasRegionIreland() {
    assertEquals("us-east-1", amazonConfiguration.s3().getRegionName());
  }

  @Test
  void s3hasThreeBuckets() {
    var buckets = amazonConfiguration.s3().listBuckets();
    assertEquals(3, buckets.size());
  }
}
