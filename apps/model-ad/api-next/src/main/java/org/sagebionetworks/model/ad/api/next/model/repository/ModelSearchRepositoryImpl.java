package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.sagebionetworks.model.ad.api.next.model.document.SearchResultDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ModelSearchRepositoryImpl implements ModelSearchRepository {

  private static final String MOUSE_COLLECTION = "model_details";
  private static final String MARMOSET_COLLECTION = "marmo_details";

  private final MongoTemplate mongoTemplate;

  @Override
  public List<SearchResultDocument> searchModels(String query, List<ModelOrganismDto> organisms) {
    String escapedQuery = Pattern.quote(query);
    boolean includeMouse = organisms.contains(ModelOrganismDto.MOUSE);
    boolean includeMarmoset = organisms.contains(ModelOrganismDto.MARMOSET);

    if (includeMouse && includeMarmoset) {
      return searchBothOrganisms(escapedQuery);
    } else if (includeMouse) {
      return searchSingleOrganism(escapedQuery, MOUSE_COLLECTION,
          mouseMatchBranches(escapedQuery), ModelOrganismDto.MOUSE);
    } else if (includeMarmoset) {
      return searchSingleOrganism(escapedQuery, MARMOSET_COLLECTION,
          marmosetMatchBranches(escapedQuery), ModelOrganismDto.MARMOSET);
    }
    return List.of();
  }

  private List<SearchResultDocument> searchBothOrganisms(String escapedQuery) {
    List<AggregationOperation> operations = new ArrayList<>();
    operations.add(
        addFieldsStage(mouseMatchBranches(escapedQuery), escapedQuery, ModelOrganismDto.MOUSE));
    operations.add(matchNonNull());
    operations.add(unionWithStage(escapedQuery));
    operations.add(sortStage());
    operations.add(projectStage());

    Aggregation aggregation = Aggregation.newAggregation(operations).withOptions(
        AggregationOptions.builder().allowDiskUse(true).build());

    log.debug("Executing search aggregation on {} with $unionWith {}", MOUSE_COLLECTION,
        MARMOSET_COLLECTION);
    AggregationResults<SearchResultDocument> results =
        mongoTemplate.aggregate(aggregation, MOUSE_COLLECTION, SearchResultDocument.class);
    return results.getMappedResults();
  }

  private List<SearchResultDocument> searchSingleOrganism(String escapedQuery, String collection,
      List<Document> matchBranches, ModelOrganismDto organism) {
    List<AggregationOperation> operations = new ArrayList<>();
    operations.add(addFieldsStage(matchBranches, escapedQuery, organism));
    operations.add(matchNonNull());
    operations.add(sortStage());
    operations.add(projectStage());

    Aggregation aggregation = Aggregation.newAggregation(operations).withOptions(
        AggregationOptions.builder().allowDiskUse(true).build());

    log.debug("Executing search aggregation on {}", collection);
    AggregationResults<SearchResultDocument> results =
        mongoTemplate.aggregate(aggregation, collection, SearchResultDocument.class);
    return results.getMappedResults();
  }

  private AggregationOperation addFieldsStage(List<Document> matchBranches, String escapedQuery,
      ModelOrganismDto organism) {
    return context -> new Document("$addFields", new Document()
        .append("match_info", new Document("$let", new Document()
            .append("vars", buildVars(escapedQuery))
            .append("in", new Document("$switch", new Document()
                .append("branches", matchBranches)
                .append("default", null)))))
        .append("model_organism", organism.getValue()));
  }

  private AggregationOperation matchNonNull() {
    return context -> new Document("$match",
        new Document("match_info", new Document("$ne", null)));
  }

  private AggregationOperation unionWithStage(String escapedQuery) {
    List<Document> marmosetPipeline = Arrays.asList(
        new Document("$addFields", new Document()
            .append("match_info", new Document("$let", new Document()
                .append("vars", buildVars(escapedQuery))
                .append("in", new Document("$switch", new Document()
                    .append("branches", marmosetMatchBranches(escapedQuery))
                    .append("default", null)))))
            .append("model_organism", ModelOrganismDto.MARMOSET.getValue())),
        new Document("$match",
            new Document("match_info", new Document("$ne", null))));

    return context -> new Document("$unionWith", new Document()
        .append("coll", MARMOSET_COLLECTION)
        .append("pipeline", marmosetPipeline));
  }

  private AggregationOperation sortStage() {
    return context -> new Document("$sort", new Document()
        .append("match_info.precedence", 1)
        .append("name", 1));
  }

  private AggregationOperation projectStage() {
    return context -> new Document("$project", new Document()
        .append("_id", "$name")
        .append("match_field", "$match_info.match_field")
        .append("match_value", "$match_info.match_value")
        .append("model_organism", "$model_organism"));
  }

  private Document buildVars(String escapedQuery) {
    return new Document("filtered_aliases", new Document("$filter", new Document()
        .append("input", new Document("$ifNull", Arrays.asList("$aliases", List.of())))
        .append("cond", new Document("$regexMatch", new Document()
            .append("input", "$$this")
            .append("regex", escapedQuery)
            .append("options", "i")))));
  }

  private List<Document> mouseMatchBranches(String escapedQuery) {
    return Arrays.asList(
        new Document()
            .append("case", new Document("$regexMatch", new Document()
                .append("input", "$name").append("regex", escapedQuery)
                .append("options", "i")))
            .append("then", new Document()
                .append("precedence", 1)
                .append("match_field", "name")
                .append("match_value", "$name")),
        new Document()
            .append("case", new Document("$gt",
                Arrays.asList(new Document("$size", "$$filtered_aliases"), 0)))
            .append("then", new Document()
                .append("precedence", 2)
                .append("match_field", "aliases")
                .append("match_value",
                    new Document("$arrayElemAt", Arrays.asList("$$filtered_aliases", 0)))),
        new Document()
            .append("case", new Document("$regexMatch", new Document()
                .append("input", new Document("$ifNull", Arrays.asList("$jax_id", "")))
                .append("regex", escapedQuery).append("options", "i")))
            .append("then", new Document()
                .append("precedence", 3)
                .append("match_field", "jax_id")
                .append("match_value", "$jax_id")),
        new Document()
            .append("case", new Document("$regexMatch", new Document()
                .append("input", new Document("$ifNull", Arrays.asList("$rrid", "")))
                .append("regex", escapedQuery).append("options", "i")))
            .append("then", new Document()
                .append("precedence", 4)
                .append("match_field", "rrid")
                .append("match_value", "$rrid")));
  }

  private List<Document> marmosetMatchBranches(String escapedQuery) {
    return Arrays.asList(new Document()
        .append("case", new Document("$regexMatch", new Document()
            .append("input", "$name").append("regex", escapedQuery)
            .append("options", "i")))
        .append("then", new Document()
            .append("precedence", 1)
            .append("match_field", "name")
            .append("match_value", "$name")));
  }
}
