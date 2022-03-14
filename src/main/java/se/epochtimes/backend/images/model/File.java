package se.epochtimes.backend.images.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@SecondaryTable(name = "meta",
  pkJoinColumns = @PrimaryKeyJoinColumn(name = "meta_id"))
@Table(name = "file")
public class File {

  public File(){}

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  private String filePath;
  @Embedded
  private Meta meta;
  private OffsetDateTime time;

  public File(String filePath, Meta meta) {
    this.filePath = filePath;
    this.meta = meta;
    setTime();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public OffsetDateTime getTime() {
    return time;
  }

  public void setTime() {
    this.time = OffsetDateTime.now();
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public Meta getMeta() {
    return meta;
  }

  public void setMeta(Meta meta) {
    this.meta = meta;
  }

}
