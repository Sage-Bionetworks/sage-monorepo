package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.ArrayList;
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
    // organisms is normalized by SearchApiDelegateImpl, so it is never null or empty
    String escapedQuery = Pattern.quote(query);
    List<SearchResultDocument> results = new ArrayList<>();

    // no DocumentDB version supports $unionWith as of 2026-09-01, so we merge the results in Java
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
    Document switchExpression = new Document("$switch", new Document()
        .append("branches", matchBranches)
        .append("default", null));
    // only mouse branches read $$filtered_aliases; add the $let back if marmoset gains aliases
    Document matchExpression = organism == ModelOrganismDto.MOUSE
        ? new Document("$let", new Document()
            .append("vars", buildVars(escapedQuery))
            .append("in", switchExpression))
        : switchExpression;
    return context -> new Document("$addFields", new Document()
        .append("match_info", matchExpression)
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
        .append("input", "$aliases")
        .append("cond", new Document("$regexMatch", new Document()
            .append("input", "$$this")
            .append("regex", escapedQuery)
            .append("options", "i")))));
  }

  private List<Document> mouseMatchBranches(String escapedQuery) {
    return List.of(
        regexBranch(MatchField.NAME, escapedQuery),
        aliasesBranch(),
        regexBranch(MatchField.JAX_ID, escapedQuery),
        regexBranch(MatchField.RRID, escapedQuery));
  }

  private List<Document> marmosetMatchBranches(String escapedQuery) {
    return List.of(regexBranch(MatchField.NAME, escapedQuery));
  }

  private Document regexBranch(MatchField matchField, String escapedQuery) {
    String fieldPath = "$" + matchField.getField();
    return new Document()
        .append("case", new Document("$regexMatch", new Document()
            .append("input", fieldPath)
            .append("regex", escapedQuery)
            .append("options", "i")))
        .append("then", thenDocument(matchField, fieldPath));
  }

  private Document aliasesBranch() {
    return new Document()
        .append("case", new Document("$gt",
            List.of(new Document("$size", "$$filtered_aliases"), 0)))
        .append("then", thenDocument(MatchField.ALIASES,
            new Document("$arrayElemAt", List.of("$$filtered_aliases", 0))));
  }

  private Document thenDocument(MatchField matchField, Object matchValue) {
    return new Document()
        .append("precedence", matchField.getPrecedence())
        .append("match_field", matchField.getField())
        .append("match_value", matchValue);
  }

  enum MatchField {
    NAME("name", 1),
    ALIASES("aliases", 2),
    JAX_ID("jax_id", 3),
    RRID("rrid", 4);

    private final String field;
    private final int precedence;

    MatchField(String field, int precedence) {
      this.field = field;
      this.precedence = precedence;
    }

    String getField() {
      return field;
    }

    int getPrecedence() {
      return precedence;
    }
  }
}
