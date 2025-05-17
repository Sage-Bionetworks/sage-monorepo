package org.sagebionetworks.agora.gene.api.model.document;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class BioDomainDocument {

  // TODO: Be consistent with the conversion between camelCase and snake_case)
  @Field("biodomain")
  private String bioDomain;
}
