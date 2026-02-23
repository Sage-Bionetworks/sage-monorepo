package org.sagebionetworks.agora.api.next.model.document;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "nominateddrugs")
public class NominatedDrugDocument {

  @Id
  private ObjectId id;

  @Field("common_name")
  private String commonName;

  @Field("total_nominations")
  private Integer totalNominations;

  @Field("year_first_nominated")
  private Integer yearFirstNominated;

  @Field("principal_investigators")
  private List<String> principalInvestigators;

  private List<String> programs;
}
