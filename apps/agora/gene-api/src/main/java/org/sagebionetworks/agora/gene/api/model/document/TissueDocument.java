package org.sagebionetworks.agora.gene.api.model.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Data
public class TissueDocument {

  private String name;

  private Float logfc;

  @Field("adj_p_val")
  private Float adjPVal;

  @Field("ci_l")
  private Float ciL;

  @Field("ci_r")
  private Float ciR;
}
