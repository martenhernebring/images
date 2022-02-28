package se.epochtimes.backend.images.model;

import javax.persistence.*;

@Entity
public class Image {

  //JPA requirements
  public Image(){}

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
