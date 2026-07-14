package org.sagebionetworks.model.ad.api.next.model.document;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Getter
@Setter
public class ModelData {

  private String name;

  @Field("evidence_type")
  private String evidenceType;

  private String tissue;

  private String age;

  private String units;

  @Field("y_axis_max")
  private BigDecimal yAxisMax;

  private List<IndividualData> data;
}
