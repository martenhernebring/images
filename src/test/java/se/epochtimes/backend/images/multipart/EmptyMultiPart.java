package se.epochtimes.backend.images.multipart;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class EmptyMultiPart implements MultipartFile {
  @Override
  public String getName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getOriginalFilename() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getContentType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public long getSize() {
    return 0L;
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
  public void transferTo(File dest)
    throws IOException, IllegalStateException {
    throw new IOException("Unsupported");
  }
}
