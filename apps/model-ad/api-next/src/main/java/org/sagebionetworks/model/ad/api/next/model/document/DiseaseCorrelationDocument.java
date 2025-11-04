package org.sagebionetworks.model.ad.api.next.model.document;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "disease_correlation")
public class DiseaseCorrelationDocument {

  @Id
  private ObjectId id;

  private String name;

  @Field("matched_control")
  private String matchedControl;

  @Field("model_type")
  private String modelType;

  @Field("modified_genes")
  private List<String> modifiedGenes;

  private String cluster;

  private String age;

  private String sex;

  @Field("CBE")
  private @Nullable CorrelationResultDocument cbe;

  @Field("DLPFC")
  private @Nullable CorrelationResultDocument dlpfc;

  @Field("FP")
  private @Nullable CorrelationResultDocument fp;

  @Field("IFG")
  private @Nullable CorrelationResultDocument ifg;

  @Field("PHG")
  private @Nullable CorrelationResultDocument phg;

  @Field("STG")
  private @Nullable CorrelationResultDocument stg;

  @Field("TCX")
  private @Nullable CorrelationResultDocument tcx;
}
