package org.sagebionetworks.agora.api.next.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.model.document.NominatedTargetDocument;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.sagebionetworks.agora.api.next.util.ApiHelper;
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
public class CustomNominatedTargetRepositoryImpl implements CustomNominatedTargetRepository {

  private final MongoTemplate mongoTemplate;

  private static final String PRIMARY_FIELD = "hgnc_symbol";

  @Override
  public Page<NominatedTargetDocument> findAll(
    Pageable pageable,
    NominatedTargetSearchQueryDto query,
    List<String> items
  ) {
    Query mongoQuery = new Query();
    List<Criteria> andCriteria = new ArrayList<>();

    ItemFilterTypeQueryDto filterType = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );
    String search = query.getSearch();

    // Add data filters (AND between fields, OR within field)
    addDataFilterCriteria(
      query.getCohortStudies(),
      query.getInputData(),
      query.getInitialNomination(),
      query.getNominatingTeams(),
      query.getPharosClass(),
      query.getPrograms(),
      query.getTotalNominations(),
      andCriteria
    );

    // Add name filtering (items + itemFilterType)
    addHgncSymbolFilterCriteria(items, filterType, andCriteria);

    // Add search filtering (only when itemFilterType is EXCLUDE)
    addSearchFilterCriteria(search, filterType, andCriteria);

    // Combine all criteria with AND
    if (!andCriteria.isEmpty()) {
      mongoQuery.addCriteria(new Criteria().andOperator(andCriteria.toArray(new Criteria[0])));
    }

    mongoQuery.with(pageable);

    log.debug("Executing MongoDB query: {}", mongoQuery);

    // Execute query
    List<NominatedTargetDocument> results = mongoTemplate.find(
      mongoQuery,
      NominatedTargetDocument.class
    );

    // Count total for pagination (without limit/skip)
    long total = mongoTemplate.count(
      Query.of(mongoQuery).limit(-1).skip(-1),
      NominatedTargetDocument.class
    );

    return new PageImpl<>(results, pageable, total);
  }

  private void addDataFilterCriteria(
    List<String> cohortStudies,
    List<String> inputData,
    List<Integer> initialNomination,
    List<String> nominatingTeams,
    List<String> pharosClass,
    List<String> programs,
    List<Integer> totalNominations,
    List<Criteria> andCriteria
  ) {
    // cohort_studies: array field - use $in (matches if array contains any value)
    if (cohortStudies != null && !cohortStudies.isEmpty()) {
      andCriteria.add(Criteria.where("cohort_studies").in(cohortStudies));
    }

    // input_data: array field - use $in (matches if array contains any value)
    if (inputData != null && !inputData.isEmpty()) {
      andCriteria.add(Criteria.where("input_data").in(inputData));
    }

    // initialNomination: array field - use $in (matches if array contains any value)
    if (initialNomination != null && !initialNomination.isEmpty()) {
      andCriteria.add(Criteria.where("initial_nomination").in(initialNomination));
    }

    // nominatingTeams: array field - use $in (matches if array contains any value)
    if (nominatingTeams != null && !nominatingTeams.isEmpty()) {
      andCriteria.add(Criteria.where("nominating_teams").in(nominatingTeams));
    }

    // pharosClass: array field - use $in (matches if array contains any value)
    if (pharosClass != null && !pharosClass.isEmpty()) {
      andCriteria.add(Criteria.where("pharos_class").in(pharosClass));
    }

    // programs: array field - use $in (matches if array contains any value)
    if (programs != null && !programs.isEmpty()) {
      andCriteria.add(Criteria.where("programs").in(programs));
    }

    // totalNominations: array field - use $in (matches if array contains any value)
    if (totalNominations != null && !totalNominations.isEmpty()) {
      andCriteria.add(Criteria.where("total_nominations").in(totalNominations));
    }
  }

  private void addHgncSymbolFilterCriteria(
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

    if (filterType == ItemFilterTypeQueryDto.INCLUDE) {
      andCriteria.add(Criteria.where(PRIMARY_FIELD).in(items));
    } else {
      andCriteria.add(Criteria.where(PRIMARY_FIELD).nin(items));
    }
  }

  private void addSearchFilterCriteria(
    String search,
    ItemFilterTypeQueryDto filterType,
    List<Criteria> andCriteria
  ) {
    // Search only applies when itemFilterType is EXCLUDE
    if (filterType != ItemFilterTypeQueryDto.EXCLUDE || search == null || search.trim().isEmpty()) {
      return;
    }

    String trimmedSearch = search.trim();
    if (trimmedSearch.contains(",")) {
      // Comma-separated list: case-insensitive full matches
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      andCriteria.add(Criteria.where(PRIMARY_FIELD).in(patterns));
    } else {
      // Single term: case-insensitive partial match
      String quotedSearch = Pattern.quote(trimmedSearch);
      andCriteria.add(Criteria.where(PRIMARY_FIELD).regex(quotedSearch, "i"));
    }
  }
}
