package org.sagebionetworks.qtl.api.model.document;

import java.math.BigDecimal;
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
@Document(collection = "eqtl_phenotype")
public class EqtlPhenotypeDocument {

  @Id
  private ObjectId id;

  private String variant;

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  @Field("cell_type_id")
  private String cellTypeId;

  @Field("expression_summaries")
  private List<GenotypeExpressionSummary> expressionSummaries;

  @Field("effect_size_cell_class_data")
  private List<CellClassEffectSize> effectSizeCellClassData;

  @Getter
  @Setter
  @NoArgsConstructor
  public static class GenotypeExpressionSummary {

    private String genotype;

    private BigDecimal min;

    @Field("first_quartile")
    private BigDecimal firstQuartile;

    private BigDecimal median;

    @Field("third_quartile")
    private BigDecimal thirdQuartile;

    private BigDecimal max;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class CellClassEffectSize {

    @Field("cell_type_id")
    private String cellTypeId;

    @Field("cell_class")
    private String cellClass;

    @Field("cell_class_short")
    private String cellClassShort;

    private BigDecimal value;

    @Field("ci_left")
    private BigDecimal ciLeft;

    @Field("ci_right")
    private BigDecimal ciRight;
  }
}
