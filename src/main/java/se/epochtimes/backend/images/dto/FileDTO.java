package se.epochtimes.backend.images.dto;

import se.epochtimes.backend.images.model.File;
import se.epochtimes.backend.images.model.Meta;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class FileDTO implements Serializable {

  private final OffsetDateTime time;
  private final String filePath;
  private final Meta meta;

  public FileDTO(OffsetDateTime time,
                 String filePath,
                 Meta meta) {
    this.time = time;
    this.filePath = filePath;
    this.meta = meta;
  }

  public FileDTO(File file) {
    this.time = file.getTime();
    this.filePath = file.getFilePath();
    this.meta = file.getMeta();
  }

  public OffsetDateTime getTime() {
    return time;
  }

  public String getFilePath() {
    return filePath;
  }

  public Meta getMeta() {
    return meta;
  }
}
