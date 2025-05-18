package org.sagebionetworks.agora.gene.api.model.document;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class TargetNominationDocument {

  @Field("initial_nomination")
  private int initialNomination;

  private String team;
}
