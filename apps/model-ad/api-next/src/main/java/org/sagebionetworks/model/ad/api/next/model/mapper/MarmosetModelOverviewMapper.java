package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.MarmosetModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarmosetModelOverviewMapper {

  private final LinkMapper linkMapper;

  public MarmosetModelOverviewDto toDto(@Nullable MarmosetModelOverviewDocument document) {
    if (document == null) {
      return null;
    }

    List<String> modifiedGenes = document.getModifiedGenes() == null
      ? List.of()
      : List.copyOf(document.getModifiedGenes());
    List<MarmosetModelOverviewDto.AvailableDataEnum> availableData =
      document.getAvailableData() == null
        ? List.of()
        : document
          .getAvailableData()
          .stream()
          .map(MarmosetModelOverviewDto.AvailableDataEnum::fromValue)
          .toList();

    MarmosetModelOverviewDto dto = new MarmosetModelOverviewDto(
      document.getId() != null ? document.getId().toHexString() : null,
      document.getName(),
      document.getModelType(),
      linkMapper.toRequiredDto(document.getStudyData()),
      modifiedGenes,
      availableData
    );

    dto.setBiomarkers(linkMapper.toNullableDto(document.getBiomarkers()));
    return dto;
  }
}
