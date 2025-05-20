package org.sagebionetworks.agora.gene.api.model.document;

import com.mongodb.lang.Nullable;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "geneinfo")
// TODO: adopt name consistent with the DB collection name ()
// I'm currently using the name used in agora-api (with the Document suffix).
public class GeneDocument {

  @Id
  public String id;

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  @Field("hgnc_symbol")
  private String hgncSymbol;

  @Field("is_igap")
  private boolean isIgap;

  @Field("is_eqtl")
  private boolean isEqtl;

  @Field("rna_brain_change_studied")
  private boolean rnaBrainChangeStudied;

  @Field("is_any_rna_changed_in_ad_brain")
  private boolean isAnyRnaChangedInAdBrain;

  @Field("protein_brain_change_studied")
  private boolean proteinBrainChangeStudied;

  @Field("is_any_protein_changed_in_ad_brain")
  private boolean isAnyProteinChangedInAdBrain;

  @Nullable
  @Field("is_adi")
  private Boolean isAdi;

  @Nullable
  @Field("is_tep")
  private Boolean isTep;

  @Nullable
  @Field("total_nominations")
  private Integer totalNominations;

  @Nullable
  @Field("target_nominations")
  private List<TargetNominationDocument> targetNominations;
}
