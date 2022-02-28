package se.epochtimes.backend.images.model;

public enum Subject {
  EKONOMI(0);

  private final String print;
  private final int code;

  Subject(int code) {
    this.code = code;
    this.print = "EKONOMI";
  }

  public String getPrint() {
    return print;
  }
  public int getCode() {
    return code;
  }
  public static Subject valueOf(int code) {
    for (Subject value : Subject.values()) {
      if (value.getCode() == code) {
        return value;
      }
    }
    throw new IllegalArgumentException("Invalid Employee Status code.");
  }
}
