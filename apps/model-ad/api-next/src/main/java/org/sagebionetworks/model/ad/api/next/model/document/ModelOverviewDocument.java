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
@Document(collection = "model_overview")
public class ModelOverviewDocument {

  @Id
  private ObjectId id;

  private String name;

  @Field("model_type")
  private String modelType;

  @Field("matched_controls")
  private List<String> matchedControls;

  @Field("gene_expression")
  private @Nullable ModelOverviewLinkDocument geneExpression;

  @Field("disease_correlation")
  private @Nullable ModelOverviewLinkDocument diseaseCorrelation;

  @Field("biomarkers")
  private @Nullable ModelOverviewLinkDocument biomarkers;

  @Field("pathology")
  private @Nullable ModelOverviewLinkDocument pathology;

  @Field("study_data")
  private ModelOverviewLinkDocument studyData;

  @Field("jax_strain")
  private ModelOverviewLinkDocument jaxStrain;

  private ModelOverviewLinkDocument center;

  @Field("modified_genes")
  private List<String> modifiedGenes;

  @Field("available_data")
  private List<String> availableData;
}
