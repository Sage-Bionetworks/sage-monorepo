package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIdentifier;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomGeneExpressionRepositoryImpl implements CustomGeneExpressionRepository {

  private final MongoTemplate mongoTemplate;

  @Override
  public Page<GeneExpressionDocument> findAll(
    Pageable pageable,
    GeneExpressionSearchQueryDto query,
    List<String> items,
    String tissue,
    String sexCohort
  ) {
    Query mongoQuery = new Query();
    List<Criteria> andCriteria = new ArrayList<>();

    ItemFilterTypeQueryDto filterType = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );
    String search = query.getSearch();

    // Add tissue and sex_cohort filters (always required)
    addTissueAndSexCohortCriteria(tissue, sexCohort, andCriteria);

    // Add data filters (AND between fields, OR within field)
    addDataFilterCriteria(
      query.getBiodomains(),
      query.getModelType(),
      query.getName(),
      andCriteria
    );

    // Add composite identifier filtering (items + itemFilterType)
    addCompositeIdentifierFilterCriteria(items, filterType, andCriteria);

    // Add search filtering (gene_symbol search, only when itemFilterType is EXCLUDE)
    addSearchFilterCriteria(search, filterType, andCriteria);

    // Combine all criteria with AND
    if (!andCriteria.isEmpty()) {
      mongoQuery.addCriteria(new Criteria().andOperator(andCriteria.toArray(new Criteria[0])));
    }

    mongoQuery.with(pageable);

    log.debug("Executing MongoDB query: {}", mongoQuery);

    // Execute query
    List<GeneExpressionDocument> results = mongoTemplate.find(
      mongoQuery,
      GeneExpressionDocument.class
    );

    // Count total for pagination (without limit/skip)
    long total = mongoTemplate.count(
      Query.of(mongoQuery).limit(-1).skip(-1),
      GeneExpressionDocument.class
    );

    return new PageImpl<>(results, pageable, total);
  }

  private void addTissueAndSexCohortCriteria(
    String tissue,
    String sexCohort,
    List<Criteria> andCriteria
  ) {
    andCriteria.add(Criteria.where("tissue").is(tissue));
    andCriteria.add(Criteria.where("sex_cohort").is(sexCohort));
  }

  private void addDataFilterCriteria(
    List<String> biodomains,
    List<String> modelType,
    List<String> name,
    List<Criteria> andCriteria
  ) {
    // biodomains: array field - use $in (matches if array contains any value)
    if (biodomains != null && !biodomains.isEmpty()) {
      andCriteria.add(Criteria.where("biodomains").in(biodomains));
    }

    // model_type: string field - use $in (matches if value equals any)
    if (modelType != null && !modelType.isEmpty()) {
      andCriteria.add(Criteria.where("model_type").in(modelType));
    }

    // name: string field - use $in (matches if value equals any)
    if (name != null && !name.isEmpty()) {
      andCriteria.add(Criteria.where("name").in(name));
    }
  }

  private void addCompositeIdentifierFilterCriteria(
    List<String> items,
    ItemFilterTypeQueryDto filterType,
    List<Criteria> andCriteria
  ) {
    if (items.isEmpty()) {
      // For INCLUDE mode with empty items, add impossible condition to return empty results
      if (filterType == ItemFilterTypeQueryDto.INCLUDE) {
        andCriteria.add(Criteria.where("_id").is(null));
      }
      // For EXCLUDE mode with empty items, no filtering needed (return all)
      return;
    }

    // Parse composite identifiers (format: ensembl_gene_id~name)
    List<GeneExpressionIdentifier> identifiers = parseIdentifiers(items);

    // Build criteria for each identifier (each must match both ensembl_gene_id AND name)
    List<Criteria> compositeConditions = new ArrayList<>();
    for (GeneExpressionIdentifier identifier : identifiers) {
      Criteria idCondition = new Criteria()
        .andOperator(
          Criteria.where("ensembl_gene_id").is(identifier.getEnsemblGeneId()),
          Criteria.where("name").is(identifier.getName())
        );
      compositeConditions.add(idCondition);
    }

    // Apply INCLUDE or EXCLUDE logic
    if (filterType == ItemFilterTypeQueryDto.INCLUDE) {
      // Match ANY of the composite identifiers ($or)
      andCriteria.add(new Criteria().orOperator(compositeConditions.toArray(new Criteria[0])));
    } else {
      // Match NONE of the composite identifiers ($nor)
      andCriteria.add(new Criteria().norOperator(compositeConditions.toArray(new Criteria[0])));
    }
  }

  private List<GeneExpressionIdentifier> parseIdentifiers(List<String> items) {
    try {
      return items.stream().map(GeneExpressionIdentifier::parse).toList();
    } catch (InvalidFilterException e) {
      log.error("Failed to parse composite identifiers: {}", e.getMessage());
      throw e;
    }
  }

  private void addSearchFilterCriteria(
    String search,
    ItemFilterTypeQueryDto filterType,
    List<Criteria> andCriteria
  ) {
    // Search only applies when itemFilterType is EXCLUDE
    if (
      filterType != ItemFilterTypeQueryDto.EXCLUDE || search == null || search.trim().isEmpty()
    ) {
      return;
    }

    String trimmedSearch = search.trim();
    if (trimmedSearch.contains(",")) {
      // Comma-separated list: case-insensitive full matches
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      andCriteria.add(Criteria.where("gene_symbol").in(patterns));
    } else {
      // Single term: case-insensitive partial match
      String quotedSearch = Pattern.quote(trimmedSearch);
      andCriteria.add(Criteria.where("gene_symbol").regex(quotedSearch, "i"));
    }
  }
}
