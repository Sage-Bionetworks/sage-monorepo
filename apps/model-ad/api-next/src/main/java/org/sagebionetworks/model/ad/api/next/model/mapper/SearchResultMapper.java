package org.sagebionetworks.model.ad.api.next.model.mapper;

import org.sagebionetworks.model.ad.api.next.model.document.SearchResultDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SearchResultDto;
import org.springframework.stereotype.Component;

@Component
public class SearchResultMapper {

  public SearchResultDto toDto(SearchResultDocument document) {
    return SearchResultDto.builder()
        .id(document.getId())
        .matchField(document.getMatchField())
        .matchValue(document.getMatchValue())
        .modelOrganism(ModelOrganismDto.fromValue(document.getModelOrganism()))
        .build();
  }
}
