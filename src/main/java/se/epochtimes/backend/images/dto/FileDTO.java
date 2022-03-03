package se.epochtimes.backend.images.dto;

import se.epochtimes.backend.images.model.file.Meta;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record FileDTO(OffsetDateTime time,
                      String filePath,
                      Meta meta) implements Serializable {
}
