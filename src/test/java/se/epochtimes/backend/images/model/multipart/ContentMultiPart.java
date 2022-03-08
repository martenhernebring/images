package se.epochtimes.backend.images.model.multipart;

import org.apache.http.entity.ContentType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public record ContentMultiPart(ContentType contentType) implements MultipartFile {

  @Override
  public String getName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getOriginalFilename() {
    return "original.jpg";
  }

  @Override
  public String getContentType() {
    return contentType.toString();
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public long getSize() {
    return -1;
  }

  @Override
  public byte[] getBytes() throws IOException {
    throw new IOException("Unsupported");
  }

  @Override
  public InputStream getInputStream() throws IOException {
    throw new IOException("Unsupported");
  }

  @Override
  public void transferTo(File dest) throws IOException, IllegalStateException {
    throw new IOException("Unsupported");
  }
}
