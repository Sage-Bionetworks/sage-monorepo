package org.sagebionetworks.agora.api.next.model.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
public class DrugNominationEvidenceDocument {

  @Field("grant_number")
  private String grantNumber;

  @Field("contact_pi")
  private String contactPi;

  @Field("combined_with_common_name")
  private String combinedWithCommonName;

  @Field("combined_with_chembl_id")
  private String combinedWithChemblId;

  private String evidence;

  @Field("data_used")
  private String dataUsed;

  @Field("ad_moa")
  private String adMoa;

  private String reference;

  @Field("computational_validation_status")
  private String computationalValidationStatus;

  @Field("computational_validation_results")
  private String computationalValidationResults;

  @Field("experimental_validation_status")
  private String experimentalValidationStatus;

  @Field("experimental_validation_results")
  private String experimentalValidationResults;

  @Field("additional_evidence")
  private String additionalEvidence;

  private String contributors;

  @Field("initial_nomination")
  private Integer initialNomination;

  private String program;
}
