package se.epochtimes.backend.images.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.epochtimes.backend.images.service.AmazonService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AmazonService.class)
public class AmazonServiceIT {

  @Autowired
  private AmazonService amazonService;

  @Test
  void s3hasRegionIreland() {
    assertEquals("us-east-1", amazonService.s3().getRegionName());
  }

  @Test
  void s3hasThreeBuckets() {
    var buckets = amazonService.s3().listBuckets();
    assertEquals(3, buckets.size());
  }
}
