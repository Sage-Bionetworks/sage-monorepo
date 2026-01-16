package org.sagebionetworks.model.ad.api.next.model.mapper;

import org.sagebionetworks.model.ad.api.next.model.document.IndividualData;
import org.sagebionetworks.model.ad.api.next.model.dto.IndividualDataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SexDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class IndividualDataMapper {

  public IndividualDataDto toIndividualDataDto(@Nullable IndividualData individualData) {
    if (individualData == null) {
      return null;
    }

    SexDto sex = SexDto.fromValue(individualData.getSex());

    return new IndividualDataDto(
      individualData.getGenotype(),
      sex,
      individualData.getIndividualId(),
      individualData.getValue()
    );
  }
}
