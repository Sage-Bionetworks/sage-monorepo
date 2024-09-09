package org.sagebionetworks.openchallenges.kaggle.to.kafka.service.transformer;

import org.sagebionetworks.openchallenges.kafka.model.KaggleCompetitionAvroModel;
import org.sagebionetworks.openchallenges.kaggle.to.kafka.service.model.dto.KaggleCompetitionDto;
import org.springframework.stereotype.Component;

@Component
public class KaggleCompetitionToAvroTransformer {

  public KaggleCompetitionAvroModel getKaggleCompetitionAvroModelFromDto(KaggleCompetitionDto dto) {
    return KaggleCompetitionAvroModel.newBuilder()
      .setId(dto.getId())
      .setTitle(dto.getTitle())
      .build();
  }
}
