package org.sagebionetworks.model.ad.api.next.model.mapper;

import org.sagebionetworks.model.ad.api.next.model.document.DataVersionDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DataVersionDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class DataVersionMapper {

  public DataVersionDto toDto(@Nullable DataVersionDocument document) {
    if (document == null) {
      return null;
    }

    return new DataVersionDto(document.getDataFile(), document.getDataVersion());
  }
}
