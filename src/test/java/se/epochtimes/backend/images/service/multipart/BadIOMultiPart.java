package se.epochtimes.backend.images.service.multipart;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class BadIOMultiPart implements MultipartFile {
  @Override
  public String getName() {
    return "name";
  }

  @Override
  public String getOriginalFilename() {
    return "name";
  }

  @Override
  public String getContentType() {
    return "image/jpeg";
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public long getSize() {
    return 4;
  }

  @Override
  public byte[] getBytes() throws IOException {
    return new byte[4];
  }

  @Override
  public InputStream getInputStream() throws IOException {
    throw new IOException();
  }

  @Override
  public void transferTo(File dest) throws IOException, IllegalStateException {
    getInputStream();
  }
}
