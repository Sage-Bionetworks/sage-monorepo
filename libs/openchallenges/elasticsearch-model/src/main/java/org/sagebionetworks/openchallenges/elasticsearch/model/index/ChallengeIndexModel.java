package org.sagebionetworks.openchallenges.elasticsearch.model.index;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@Document(indexName = "#{elasticsearchConfigData.indexName}")
public class ChallengeIndexModel implements IndexModel {

  @JsonProperty private String id;

  @JsonProperty private Long userId;

  @JsonProperty private String text;

  @Field(
      type = FieldType.Date,
      format = {},
      pattern = "uuuu-MM-dd'T'HH:mm:ssZZ")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ssZZ")
  @JsonProperty
  private LocalDateTime createdAt;
}
