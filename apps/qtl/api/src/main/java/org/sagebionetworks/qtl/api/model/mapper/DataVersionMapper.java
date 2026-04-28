package org.sagebionetworks.qtl.api.model.mapper;

import org.sagebionetworks.qtl.api.model.document.DataVersionDocument;
import org.sagebionetworks.qtl.api.model.dto.DataVersionDto;
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
