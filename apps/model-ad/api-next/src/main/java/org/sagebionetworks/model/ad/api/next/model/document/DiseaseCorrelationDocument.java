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
  private @Nullable CorrelationResult cbe;

  @Field("DLPFC")
  private @Nullable CorrelationResult dlpfc;

  @Field("FP")
  private @Nullable CorrelationResult fp;

  @Field("IFG")
  private @Nullable CorrelationResult ifg;

  @Field("PHG")
  private @Nullable CorrelationResult phg;

  @Field("STG")
  private @Nullable CorrelationResult stg;

  @Field("TCX")
  private @Nullable CorrelationResult tcx;

  @Getter
  @Setter
  @NoArgsConstructor
  public static class CorrelationResult {

    private Double correlation;

    @Field("adj_p_val")
    private Double adjustedPvalue;
  }
}
