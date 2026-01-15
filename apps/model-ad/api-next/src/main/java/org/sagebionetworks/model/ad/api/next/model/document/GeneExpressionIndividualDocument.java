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
@Document(collection = "rna_de_individual")
public class GeneExpressionIndividualDocument {

  @Id
  private ObjectId id;

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  @Field("gene_symbol")
  private @Nullable String geneSymbol;

  private String tissue;

  private String name;

  @Field("model_group")
  private @Nullable String modelGroup;

  @Field("matched_control")
  private String matchedControl;

  private String units;

  private String age;

  @Field("age_numeric")
  private Integer ageNumeric;

  @Field("result_order")
  private List<String> resultOrder;

  private List<IndividualData> data;
}
