package org.sagebionetworks.model.ad.api.next.model.mapper;

import org.sagebionetworks.model.ad.api.next.model.document.IndividualData;
import org.sagebionetworks.model.ad.api.next.model.dto.IndividualDataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SexDto;
import org.springframework.stereotype.Component;

@Component
public class IndividualDataMapper {

  public IndividualDataDto toIndividualDataDto(IndividualData individualData) {
    SexDto sex = SexDto.fromValue(individualData.getSex());

    return new IndividualDataDto(
      individualData.getGenotype(),
      sex,
      individualData.getIndividualId(),
      individualData.getValue()
    );
  }
}
