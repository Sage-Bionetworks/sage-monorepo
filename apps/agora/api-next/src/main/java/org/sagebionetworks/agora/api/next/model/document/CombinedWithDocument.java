package org.sagebionetworks.agora.api.next.model.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
public class CombinedWithDocument {

  @Field("common_name")
  private String commonName;

  @Field("chembl_id")
  private String chemblId;
}
