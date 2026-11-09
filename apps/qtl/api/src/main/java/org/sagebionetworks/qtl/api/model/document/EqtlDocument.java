package org.sagebionetworks.qtl.api.model.document;

import java.math.BigDecimal;
import lombok.Builder;
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
@Document(collection = "eqtl_results")
public class EqtlDocument {

  @Id
  private ObjectId id;

  private String variant;

  @Field("variant_location")
  private GenomicLocation variantLocation;

  @Field("effect_direction")
  private String effectDirection;

  @Field("gene_symbol")
  private String geneSymbol;

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  @Field("gene_location")
  private GenomicLocation geneLocation;

  private BigDecimal beta;

  @Field("adj_p_val")
  private String adjPVal;

  @Field("adj_p_val_numeric")
  private BigDecimal adjPValNumeric;

  private BigDecimal maf;

  @Field("minor_allele")
  private String minorAllele;

  @Field("reference_allele")
  private String referenceAllele;

  @Field("cell_type_id")
  private String cellTypeId;

  @Field("cell_class")
  private String cellClass;

  @Field("cell_class_short")
  private String cellClassShort;

  @Field("cell_subclass")
  private String cellSubclass;

  @Field("cell_subclass_short")
  private String cellSubclassShort;

  @Builder
  @Getter
  @Setter
  public static class GenomicLocation {

    private String chr;

    private Integer start;

    private Integer end;
  }
}
