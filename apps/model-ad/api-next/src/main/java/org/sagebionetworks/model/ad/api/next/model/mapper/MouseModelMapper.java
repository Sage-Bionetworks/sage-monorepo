package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.MouseModelDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneticInfoDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.OrganismDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MouseModelMapper {

  private final GeneticInfoMapper geneticInfoMapper;
  private final ModelDataMapper modelDataMapper;

  public ModelDto toDto(@Nullable MouseModelDocument document) {
    if (document == null) {
      return null;
    }

    List<String> matchedControls = document.getMatchedControls() == null
      ? List.of()
      : List.copyOf(document.getMatchedControls());

    List<String> aliases = document.getAliases() == null
      ? List.of()
      : List.copyOf(document.getAliases());

    List<GeneticInfoDto> geneticInfo = document.getGeneticInfo() == null
      ? List.of()
      : document.getGeneticInfo().stream().map(geneticInfoMapper::toDto).toList();

    List<ModelDataDto> biomarkers = document.getBiomarkers() == null
      ? List.of()
      : document.getBiomarkers().stream().map(modelDataMapper::toDto).toList();

    List<ModelDataDto> pathology = document.getPathology() == null
      ? List.of()
      : document.getPathology().stream().map(modelDataMapper::toDto).toList();

    return new MouseModelDto(
      OrganismDto.MOUSE.getValue(),
      document.getName(),
      matchedControls,
      document.getModelType(),
      document.getContributingGroup(),
      document.getStudySynid(),
      document.getRrid(),
      document.getJaxId(),
      document.getAlzforumId(),
      document.getGenotype(),
      aliases,
      document.getTranscriptomics(),
      document.getDiseaseCorrelation(),
      document.getSpatialTranscriptomics(),
      geneticInfo,
      biomarkers,
      pathology
    );
  }
}
