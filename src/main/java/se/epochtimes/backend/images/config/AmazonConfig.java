package se.epochtimes.backend.images.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AmazonConfig {

  @Bean
  public AmazonS3 s3() {
    Map<String,String> env = System.getenv();
    return AmazonS3ClientBuilder
      .standard()
      .withRegion("eu-west-1")
      .withCredentials(new AWSStaticCredentialsProvider(
        new BasicAWSCredentials(env.get("ACCESS_KEY"), env.get("SECRET_KEY"))))
      .build();
  }
}
