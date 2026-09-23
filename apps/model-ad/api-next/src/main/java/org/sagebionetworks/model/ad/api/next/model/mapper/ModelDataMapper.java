package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.ModelData;
import org.sagebionetworks.model.ad.api.next.model.dto.IndividualDataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDataDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelDataMapper {

  private final IndividualDataMapper individualDataMapper;

  public ModelDataDto toDto(@Nullable ModelData modelData) {
    if (modelData == null) {
      return null;
    }

    List<String> resultOrder = modelData.getResultOrder() == null
      ? List.of()
      : List.copyOf(modelData.getResultOrder());

    List<IndividualDataDto> data = modelData.getData() == null
      ? List.of()
      : modelData.getData().stream().map(individualDataMapper::toIndividualDataDto).toList();

    return new ModelDataDto(
      modelData.getName(),
      modelData.getEvidenceType(),
      modelData.getAge(),
      modelData.getUnits(),
      modelData.getYAxisMax(),
      resultOrder,
      data
    ).tissue(modelData.getTissue());
  }
}
