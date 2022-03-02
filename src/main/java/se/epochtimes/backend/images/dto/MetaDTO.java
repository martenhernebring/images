package se.epochtimes.backend.images.dto;

import java.time.OffsetDateTime;

public record MetaDTO(OffsetDateTime time, String contentMd5, String eTag, String versionId) {
}
