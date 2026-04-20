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

  @Field("chembl_id")
  private String chemblId;

  @Field("total_nominations")
  private Integer totalNominations;

  @Field("combined_with")
  private LinkDocument combinedWith;

  @Field("initial_nomination")
  private Integer initialNomination;

  @Field("principal_investigators")
  private List<String> principalInvestigators;

  private List<String> programs;

  private String modality;

  @Field("year_of_first_approval")
  private Integer yearOfFirstApproval;

  @Field("maximum_clinical_trial_phase")
  private String maximumClinicalTrialPhase;
}
