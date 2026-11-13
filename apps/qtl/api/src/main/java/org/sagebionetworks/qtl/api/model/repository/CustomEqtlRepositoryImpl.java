package org.sagebionetworks.qtl.api.model.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.explorers.ComparisonToolRepositorySupport;
import org.sagebionetworks.explorers.CtFilterConfig;
import org.sagebionetworks.explorers.CtPage;
import org.sagebionetworks.explorers.CtQueryOptions;
import org.sagebionetworks.qtl.api.model.document.EqtlDocument;
import org.sagebionetworks.qtl.api.model.dto.EqtlIdentifier;
import org.sagebionetworks.qtl.api.model.dto.EqtlSearchQueryDto;
import org.sagebionetworks.qtl.api.model.dto.ItemFilterTypeQueryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

/**
 * Custom repository implementation using MongoDB aggregation pipeline.
 *
 * <p>Uses aggregation to support case-insensitive sorting and complex filtering logic. The pipeline
 * scaffold (count, $match, $addFields, $sort, $skip, $limit) lives in
 * {@link ComparisonToolRepositorySupport}.
 */
@Repository
@Slf4j
public class CustomEqtlRepositoryImpl
  extends ComparisonToolRepositorySupport<EqtlDocument>
  implements CustomEqtlRepository {

  private static final String COLLECTION_NAME = "eqtl_results";
  private static final String VARIANT_FIELD = "variant";

  /**
   * The fields a search term is matched against. These are raw Mongo field names, which is what
   * the count query needs: {@code executePagedAggregation} counts via
   * {@code mongoTemplate.count(query, collectionName)}, which performs no {@code @Field} name
   * translation.
   */
  private static final List<String> SEARCH_FIELDS = List.of(
    VARIANT_FIELD,
    "gene_symbol",
    "ensembl_gene_id"
  );

  public CustomEqtlRepositoryImpl(MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  protected Class<EqtlDocument> getDocumentClass() {
    return EqtlDocument.class;
  }

  /**
   * Sorts the preformatted {@code adj_p_val} string by its numeric companion field.
   */
  @Override
  protected Map<String, String> getSortFieldAliases() {
    return Map.of("adj_p_val", "adj_p_val_numeric");
  }

  private final CtFilterConfig<EqtlSearchQueryDto> filterConfig = CtFilterConfig.<
    EqtlSearchQueryDto
  >builder()
    .dataFilter(VARIANT_FIELD, EqtlSearchQueryDto::getVariant)
    .dataFilter("gene_symbol", EqtlSearchQueryDto::getGene)
    .dataFilter("cell_class", EqtlSearchQueryDto::getCellClass)
    .dataFilter("cell_subclass", EqtlSearchQueryDto::getCellSubclass)
    .compositeItemFilter(item -> EqtlIdentifier.parse(item).toCriteria())
    .searchFilter(VARIANT_FIELD)
    .build();

  @Override
  protected CtFilterConfig<EqtlSearchQueryDto> getFilterConfig() {
    return filterConfig;
  }

  @Override
  public CtPage<EqtlDocument> findAll(
    Pageable pageable,
    EqtlSearchQueryDto query,
    List<String> items
  ) {
    ItemFilterTypeQueryDto filterType = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );
    CtQueryOptions options = CtQueryOptions.rowSpace(
      filterType == ItemFilterTypeQueryDto.INCLUDE,
      query.getRemainingBudget()
    ).withSelectedRowId(query.getSelectedRowId());
    Criteria matchCriteria = buildCtMatchCriteria(
      query,
      items,
      options,
      query.getSearch(),
      getFilterConfig()
    );

    return executePagedAggregation(matchCriteria, pageable, options);
  }

  /**
   * Matches the search against every field in {@link #SEARCH_FIELDS}: a comma-separated list
   * full-matches each term, and a single term partially matches, both case-insensitively.
   */
  @Override
  protected Criteria buildSearchCriteria(String field, String trimmedSearch) {
    if (trimmedSearch.contains(",")) {
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      if (patterns.isEmpty()) {
        return ApiHelper.matchNothing();
      }
      return new Criteria()
        .orOperator(
          SEARCH_FIELDS.stream()
            .map(searchField -> Criteria.where(searchField).in(patterns))
            .toList()
        );
    }
    String regex = Pattern.quote(trimmedSearch);
    return new Criteria()
      .orOperator(
        SEARCH_FIELDS.stream()
          .map(searchField -> Criteria.where(searchField).regex(regex, "i"))
          .toList()
      );
  }
}
