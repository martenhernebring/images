package se.epochtimes.backend.images.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AmazonService {

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
