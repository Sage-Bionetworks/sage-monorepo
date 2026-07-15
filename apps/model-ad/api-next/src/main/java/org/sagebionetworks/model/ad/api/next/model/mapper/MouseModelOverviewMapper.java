package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.MouseModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelOverviewDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MouseModelOverviewMapper {

  private final LinkMapper linkMapper;

  public MouseModelOverviewDto toDto(@Nullable MouseModelOverviewDocument document) {
    if (document == null) {
      return null;
    }

    List<String> matchedControls = document.getMatchedControls() == null
      ? List.of()
      : List.copyOf(document.getMatchedControls());
    List<String> modifiedGenes = document.getModifiedGenes() == null
      ? List.of()
      : List.copyOf(document.getModifiedGenes());
    List<MouseModelOverviewDto.AvailableDataEnum> availableData = document.getAvailableData() ==
      null
      ? List.of()
      : document
        .getAvailableData()
        .stream()
        .map(MouseModelOverviewDto.AvailableDataEnum::fromValue)
        .toList();

    MouseModelOverviewDto dto = new MouseModelOverviewDto(
      document.getId() != null ? document.getId().toHexString() : null,
      document.getName(),
      document.getModelType(),
      matchedControls,
      linkMapper.toRequiredDto(document.getStudyData()),
      linkMapper.toRequiredDto(document.getJaxStrain()),
      document.getCenter(),
      modifiedGenes,
      availableData
    );

    dto.setTranscriptomics(linkMapper.toNullableDto(document.getTranscriptomics()));
    dto.setDiseaseCorrelation(linkMapper.toNullableDto(document.getDiseaseCorrelation()));
    dto.setBiomarkers(linkMapper.toNullableDto(document.getBiomarkers()));
    dto.setPathology(linkMapper.toNullableDto(document.getPathology()));
    return dto;
  }
}
