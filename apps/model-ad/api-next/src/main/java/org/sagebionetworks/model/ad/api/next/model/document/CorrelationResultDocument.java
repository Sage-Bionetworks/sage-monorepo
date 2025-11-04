package org.sagebionetworks.model.ad.api.next.model.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
public class CorrelationResultDocument {

  private Double correlation;

  @Field("adj_p_val")
  private Double adjustedPvalue;
}
