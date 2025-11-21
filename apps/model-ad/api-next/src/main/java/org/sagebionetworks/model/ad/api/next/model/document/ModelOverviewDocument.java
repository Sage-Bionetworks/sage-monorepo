package org.sagebionetworks.model.ad.api.next.model.document;

import java.util.List;
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
  private @Nullable ModelOverviewLink geneExpression;

  @Field("disease_correlation")
  private @Nullable ModelOverviewLink diseaseCorrelation;

  @Field("biomarkers")
  private @Nullable ModelOverviewLink biomarkers;

  @Field("pathology")
  private @Nullable ModelOverviewLink pathology;

  @Field("study_data")
  private ModelOverviewLink studyData;

  @Field("jax_strain")
  private ModelOverviewLink jaxStrain;

  private ModelOverviewLink center;

  @Field("modified_genes")
  private List<String> modifiedGenes;

  @Field("available_data")
  private List<String> availableData;

  @Builder
  @Getter
  @Setter
  public static class ModelOverviewLink {

    @Field("link_text")
    private String linkText;

    @Field("link_url")
    private String linkUrl;
  }
}
