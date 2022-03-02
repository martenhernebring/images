package se.epochtimes.backend.images.model;

import se.epochtimes.backend.images.dto.MetaDTO;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
public class Meta {

  //JPA requirements
  public Meta(){}

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  private String contentMd5;
  private String eTag;
  private String versionId;
  private OffsetDateTime time;

  public Meta(String contentMd5, String eTag, String versionId) {
    this.contentMd5 = contentMd5;
    this.eTag = eTag;
    this.versionId = versionId;
    setTime();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getContentMd5() {
    return contentMd5;
  }

  public void setContentMd5(String contentMd5) {
    this.contentMd5 = contentMd5;
  }

  public String getETag() {
    return eTag;
  }

  public void setETag(String eTag) {
    this.eTag = eTag;
  }

  public String getVersionId() {
    return versionId;
  }

  public void setVersionId(String versionId) {
    this.versionId = versionId;
  }

  public OffsetDateTime getTime() {
    return time;
  }

  public void setTime() {
    this.time = OffsetDateTime.now();
  }
}
