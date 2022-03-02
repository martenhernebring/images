package se.epochtimes.backend.images.repository;

import org.springframework.stereotype.Repository;

public interface TextRepository {
  boolean isArticleAvailable(String header);
}
