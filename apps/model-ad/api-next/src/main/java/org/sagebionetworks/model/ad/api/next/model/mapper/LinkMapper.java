package org.sagebionetworks.model.ad.api.next.model.mapper;

import org.sagebionetworks.model.ad.api.next.exception.DataIntegrityException;
import org.sagebionetworks.model.ad.api.next.model.document.Link;
import org.sagebionetworks.model.ad.api.next.model.dto.LinkDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class LinkMapper {

  public LinkDto toRequiredDto(@Nullable Link linkDocument) {
    if (linkDocument == null) {
      throw new DataIntegrityException("Required link data is missing from the database");
    }
    return toNullableDto(linkDocument);
  }

  public LinkDto toNullableDto(@Nullable Link linkDocument) {
    if (linkDocument == null) {
      return null;
    }
    return LinkDto.builder()
      .linkText(linkDocument.getLinkText())
      .linkUrl(linkDocument.getLinkUrl())
      .build();
  }
}
