package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
    List<SearchResultDocument> results = new ArrayList<>();

    if (organisms.contains(ModelOrganismDto.MOUSE)) {
      results.addAll(searchCollection(escapedQuery, MOUSE_COLLECTION,
          mouseMatchBranches(escapedQuery), ModelOrganismDto.MOUSE));
    }
    if (organisms.contains(ModelOrganismDto.MARMOSET)) {
      results.addAll(searchCollection(escapedQuery, MARMOSET_COLLECTION,
          marmosetMatchBranches(escapedQuery), ModelOrganismDto.MARMOSET));
    }

    results.sort(Comparator.comparingInt(SearchResultDocument::getPrecedence)
        .thenComparing(SearchResultDocument::getId));
    return results;
  }

  private List<SearchResultDocument> searchCollection(String escapedQuery, String collection,
      List<Document> matchBranches, ModelOrganismDto organism) {
    List<AggregationOperation> operations = new ArrayList<>();
    operations.add(addFieldsStage(matchBranches, escapedQuery, organism));
    operations.add(matchNonNull());
    operations.add(projectStage());

    Aggregation aggregation = Aggregation.newAggregation(operations).withOptions(
        AggregationOptions.builder().allowDiskUse(true).build());

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

  private AggregationOperation projectStage() {
    return context -> new Document("$project", new Document()
        .append("_id", "$name")
        .append("match_field", "$match_info.match_field")
        .append("match_value", "$match_info.match_value")
        .append("model_organism", "$model_organism")
        .append("precedence", "$match_info.precedence"));
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
