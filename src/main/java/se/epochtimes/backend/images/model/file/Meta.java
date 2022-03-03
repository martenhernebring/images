package se.epochtimes.backend.images.model.file;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Meta implements Serializable {

  private String contentMd5;
  private String eTag;
  private String versionId;

  public Meta(String contentMd5, String eTag, String versionId) {
    this.contentMd5 = contentMd5;
    this.eTag = eTag;
    this.versionId = versionId;
  }

  //JPA requirement
  public Meta() {}

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
}
