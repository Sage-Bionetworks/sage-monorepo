package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import org.sagebionetworks.model.ad.api.next.exception.DataIntegrityException;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewLinkDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewLinkDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ModelOverviewMapper {

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
      toRequiredLinkDto(document.getStudyData()),
      toRequiredLinkDto(document.getJaxStrain()),
      toRequiredLinkDto(document.getCenter()),
      modifiedGenes,
      availableData
    );

    dto.setGeneExpression(toNullableLinkDto(document.getGeneExpression()));
    dto.setDiseaseCorrelation(toNullableLinkDto(document.getDiseaseCorrelation()));
    dto.setBiomarkers(toNullableLinkDto(document.getBiomarkers()));
    dto.setPathology(toNullableLinkDto(document.getPathology()));
    return dto;
  }

  private ModelOverviewLinkDto toRequiredLinkDto(@Nullable ModelOverviewLinkDocument linkDocument) {
    if (linkDocument == null) {
      throw new DataIntegrityException("Required link data is missing from the database");
    }
    return toNullableLinkDto(linkDocument);
  }

  private ModelOverviewLinkDto toNullableLinkDto(@Nullable ModelOverviewLinkDocument linkDocument) {
    if (linkDocument == null) {
      return null;
    }
    return ModelOverviewLinkDto.builder()
      .linkText(linkDocument.getLinkText())
      .linkUrl(linkDocument.getLinkUrl())
      .build();
  }
}
