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
@Document(collection = "druginfo")
public class DrugDocument {

  @Id
  private ObjectId id;

  @Field("common_name")
  private String commonName;

  private String description;

  @Field("iupac_id")
  private String iupacId;

  @Field("chembl_id")
  private String chemblId;

  @Field("drug_bank_id")
  private String drugBankId;

  private List<String> aliases;

  private String modality;

  @Field("year_of_first_approval")
  private Integer yearOfFirstApproval;

  @Field("maximum_clinical_trial_phase")
  private String maximumClinicalTrialPhase;

  @Field("linked_targets")
  private List<LinkedTargetDocument> linkedTargets;

  @Field("mechanisms_of_action")
  private List<String> mechanismsOfAction;

  @Field("drug_nominations")
  private List<DrugNominationEvidenceDocument> drugNominations;
}
