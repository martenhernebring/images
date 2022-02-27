package se.epochtimes.backend.images.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class FileService {
  public void save(String path,
                   String fileName,
                   InputStream inputStream,
                   Optional<Map<String, String>> optionalMetaData) {
  }
}
