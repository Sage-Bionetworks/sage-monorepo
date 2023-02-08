package org.sagebionetworks.openchallenges.kaggle.to.kafka.service.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class KaggleCompetitionDto {

  private Integer id;
  private String title;
}
