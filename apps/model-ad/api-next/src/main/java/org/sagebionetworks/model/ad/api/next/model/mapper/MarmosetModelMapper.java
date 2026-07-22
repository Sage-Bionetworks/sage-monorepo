package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.MarmosetModelDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneticInfoDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.OrganismDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarmosetModelMapper {

  private final GeneticInfoMapper geneticInfoMapper;
  private final ModelDataMapper modelDataMapper;

  public ModelDto toDto(@Nullable MarmosetModelDocument document) {
    if (document == null) {
      return null;
    }

    List<GeneticInfoDto> geneticInfo = document.getGeneticInfo() == null
      ? List.of()
      : document.getGeneticInfo().stream().map(geneticInfoMapper::toDto).toList();

    List<ModelDataDto> biomarkers = document.getBiomarkers() == null
      ? List.of()
      : document.getBiomarkers().stream().map(modelDataMapper::toDto).toList();

    return new MarmosetModelDto(
      OrganismDto.MARMOSET.getValue(),
      document.getName(),
      document.getModelType(),
      document.getStudySynid(),
      geneticInfo,
      biomarkers
    );
  }
}
