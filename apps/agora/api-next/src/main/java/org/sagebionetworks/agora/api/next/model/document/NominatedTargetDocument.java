package org.sagebionetworks.agora.api.next.model.document;

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
@Document(collection = "nominatedtargets")
public class NominatedTargetDocument {

  @Id
  private ObjectId id;

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  @Field("hgnc_symbol")
  private String hgncSymbol;

  @Field("total_nominations")
  private Integer totalNominations;

  @Field("initial_nomination")
  private Integer initialNomination;

  @Field("nominating_teams")
  private List<String> nominatingTeams;

  @Field("cohort_studies")
  private List<String> cohortStudies;

  @Field("input_data")
  private List<String> inputData;

  private List<String> programs;

  @Field("pharos_class")
  private String pharosClass;
}
