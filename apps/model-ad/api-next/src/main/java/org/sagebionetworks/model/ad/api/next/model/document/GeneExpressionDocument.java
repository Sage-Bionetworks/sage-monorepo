package org.sagebionetworks.model.ad.api.next.model.document;

import java.util.List;
import java.util.Map;
import lombok.Builder;
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
@Document(collection = "rna_de_aggregate")
public class GeneExpressionDocument {

  @Id
  private ObjectId id;

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  @Field("gene_symbol")
  private String geneSymbol;

  private List<String> biodomains;

  private String name;

  @Field("matched_control")
  private String matchedControl;

  @Field("model_group")
  private @Nullable String modelGroup;

  @Field("model_type")
  private String modelType;

  private String tissue;

  @Field("sex_cohort")
  private String sexCohort;

  @Field("4 months")
  private @Nullable FoldChangeResult fourMonths;

  @Field("12 months")
  private @Nullable FoldChangeResult twelveMonths;

  @Field("18 months")
  private @Nullable FoldChangeResult eighteenMonths;

  @Builder
  @Getter
  @Setter
  public static class FoldChangeResult {

    @Field("log2_fc")
    private Double log2Fc;

    @Field("adj_p_val")
    private Double adjPVal;
  }
}
