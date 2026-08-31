package org.sagebionetworks.model.ad.api.next.model.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
public class SearchResultDocument {

  @Id
  private String id;

  @Field("match_field")
  private String matchField;

  @Field("match_value")
  private String matchValue;

  @Field("model_organism")
  private String modelOrganism;
}
