package org.sagebionetworks.model.ad.api.next.model.mapper;

import org.sagebionetworks.model.ad.api.next.exception.DataIntegrityException;
import org.sagebionetworks.model.ad.api.next.model.document.Link;
import org.sagebionetworks.model.ad.api.next.model.dto.LinkDto;
import org.sagebionetworks.model.ad.api.next.model.dto.NamedLinkDto;
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

  public NamedLinkDto toNamedLinkDto(@Nullable Link linkDocument) {
    if (linkDocument == null) {
      throw new DataIntegrityException("Required link data is missing from the database");
    }
    if (linkDocument.getLinkText() == null) {
      throw new DataIntegrityException("Required link text is missing from the database");
    }
    return NamedLinkDto.builder()
      .linkText(linkDocument.getLinkText())
      .linkUrl(linkDocument.getLinkUrl())
      .build();
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
