package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelOverviewMapper {

  private final LinkMapper linkMapper;

  public ModelOverviewDto toDto(@Nullable ModelOverviewDocument document) {
    if (document == null) {
      return null;
    }

    List<String> matchedControls = document.getMatchedControls() == null
      ? List.of()
      : List.copyOf(document.getMatchedControls());
    List<String> modifiedGenes = document.getModifiedGenes() == null
      ? List.of()
      : List.copyOf(document.getModifiedGenes());
    List<ModelOverviewDto.AvailableDataEnum> availableData = document.getAvailableData() == null
      ? List.of()
      : document
        .getAvailableData()
        .stream()
        .map(ModelOverviewDto.AvailableDataEnum::fromValue)
        .toList();

    ModelOverviewDto dto = new ModelOverviewDto(
      document.getId() != null ? document.getId().toHexString() : null,
      document.getName(),
      document.getModelType(),
      matchedControls,
      linkMapper.toRequiredDto(document.getStudyData()),
      linkMapper.toRequiredDto(document.getJaxStrain()),
      linkMapper.toRequiredDto(document.getCenter()),
      modifiedGenes,
      availableData
    );

    dto.setGeneExpression(linkMapper.toNullableDto(document.getGeneExpression()));
    dto.setDiseaseCorrelation(linkMapper.toNullableDto(document.getDiseaseCorrelation()));
    dto.setBiomarkers(linkMapper.toNullableDto(document.getBiomarkers()));
    dto.setPathology(linkMapper.toNullableDto(document.getPathology()));
    return dto;
  }
}
