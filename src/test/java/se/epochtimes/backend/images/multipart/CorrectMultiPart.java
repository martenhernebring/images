package se.epochtimes.backend.images.multipart;

import com.amazonaws.util.IOUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CorrectMultiPart implements MultipartFile {

  @Override
  public String getName() {
    return "20220227_143031.jpg";
  }

  @Override
  public String getOriginalFilename() {
    return "20220227_143031.jpg";
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
    return 5117188;
  }

  @Override
  public byte[] getBytes() throws IOException {
    return IOUtils.toByteArray(getInputStream());
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new FileInputStream(ResourceUtils.getFile("classpath:static/images/20220227_143039.jpg"));
  }

  @Override
  public void transferTo(File dest) throws IOException, IllegalStateException {
    getInputStream();
  }
}
