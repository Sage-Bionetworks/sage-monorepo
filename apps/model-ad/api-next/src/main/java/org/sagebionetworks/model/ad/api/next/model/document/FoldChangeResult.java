package org.sagebionetworks.model.ad.api.next.model.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Getter
@Setter
public class FoldChangeResult {

  @Field("log2_fc")
  private Double log2Fc;

  @Field("adj_p_val")
  private Double adjPVal;
}
