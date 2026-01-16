package org.sagebionetworks.model.ad.api.next.model.document;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Getter
@Setter
public class IndividualData {

  private String genotype;

  private String sex;

  @Field("individual_id")
  private String individualId;

  private BigDecimal value;
}
