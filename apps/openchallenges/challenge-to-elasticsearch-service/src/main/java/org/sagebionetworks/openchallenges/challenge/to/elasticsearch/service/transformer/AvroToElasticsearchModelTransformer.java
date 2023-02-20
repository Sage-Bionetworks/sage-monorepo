package org.sagebionetworks.openchallenges.challenge.to.elasticsearch.service.transformer;

import java.util.List;
import java.util.stream.Collectors;
import org.sagebionetworks.openchallenges.elasticsearch.model.index.ChallengeIndexModel;
import org.sagebionetworks.openchallenges.kafka.model.KaggleCompetitionAvroModel;
import org.springframework.stereotype.Component;

@Component
public class AvroToElasticsearchModelTransformer {

  public List<ChallengeIndexModel> getElasticsearchModels(
      List<KaggleCompetitionAvroModel> avroModels) {
    return avroModels.stream()
        .map(
            avroModel ->
                ChallengeIndexModel.builder()
                    .userId(avroModel.getId())
                    .id(String.valueOf(avroModel.getId()))
                    .text(avroModel.getTitle())
                    .build())
        .collect(Collectors.toList());
  }
}
