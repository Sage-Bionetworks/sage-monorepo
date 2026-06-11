# Comparison Tool Repository Pattern

This rule covers every task that touches a `ComparisonToolRepositorySupport` subclass: creating a new CT repository, adding or debugging a sort column, reviewing an implementation, or understanding why rows are ordered unexpectedly.

## What is a CT repository?

Every CT table is backed by a MongoDB aggregation pipeline assembled by `ComparisonToolRepositorySupport<T>` (`libs/explorers/api-helper/`). Each product (Agora, Model-AD) has one or more concrete subclasses -- one per CT collection. The base class owns the pipeline shape; subclasses configure it by overriding a small set of hooks.

## Minimal subclass skeleton

Every CT repository follows this structure. Fill in the product-specific parts:

```java
@Repository
@Slf4j
public class CustomMyThingRepositoryImpl
  extends ComparisonToolRepositorySupport<MyThingDocument>
  implements CustomMyThingRepository {

  private static final String COLLECTION_NAME = "my_collection";

  public CustomMyThingRepositoryImpl(MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  protected Class<MyThingDocument> getDocumentClass() {
    return MyThingDocument.class;
  }

  // --- sort hooks (override only what's needed) ---

  // --- filter config ---

  private final CtFilterConfig<MyThingSearchQueryDto> filterConfig = CtFilterConfig.<
    MyThingSearchQueryDto
  >builder()
    .dataFilter("some_field", MyThingSearchQueryDto::getSomeField)
    .simpleItemFilter("name")
    .searchFilter("name")
    .build();

  @Override
  protected CtFilterConfig<MyThingSearchQueryDto> getFilterConfig() {
    return filterConfig;
  }

  // --- findAll (signature matches the custom interface) ---

  @Override
  public Page<MyThingDocument> findAll(
    Pageable pageable,
    MyThingSearchQueryDto query,
    List<String> items
  ) {
    ItemFilterTypeQueryDto filterType = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE // always default to INCLUDE if null
    );
    boolean isInclude = filterType == ItemFilterTypeQueryDto.INCLUDE;
    Criteria matchCriteria = buildCtMatchCriteria(
      query,
      items,
      isInclude,
      query.getSearch(),
      getFilterConfig()
      // add Criteria.where(...).is(...) varargs here if the collection needs base scoping
    );
    return executePagedAggregation(matchCriteria, pageable);
  }
}

```

Key points:

- `@Repository @Slf4j` on the class; constructor takes only `MongoTemplate`.
- Always `Objects.requireNonNullElse(query.getItemFilterType(), ItemFilterTypeQueryDto.INCLUDE)` -- the frontend may send null and the base class does not default it.
- `findAll` signature is defined by the custom interface, not the base class -- add product-specific parameters (tissue, cluster, etc.) there and pass them as base criteria varargs.

## Pipeline shape

The base class assembles this pipeline on every paged request:

```
$match          ← assembled from getFilterConfig() + optional base criteria
[$addFields]    ← prerequisites for computed sort fields (when needed)
[$addFields]    ← computed sort fields from getComputedSortFieldExpressions()
[$addFields]    ← isEmpty flags (null/empty rows → tail), always present when sorted
$sort           ← field names resolved via aliases and computed fields
$skip / $limit
```

Stages in brackets are omitted when not needed (e.g. no computed sort field requested, or sort is unsorted). The `allowDiskUse: true` option is set on every CT aggregation.

## Hooks to override

### Required

| Method                | What it returns                       | When to override |
| --------------------- | ------------------------------------- | ---------------- |
| `getCollectionName()` | MongoDB collection name string        | Always           |
| `getDocumentClass()`  | Java POJO class                       | Always           |
| `getFilterConfig()`   | `CtFilterConfig<Q>` built via builder | Always           |

### Sort configuration

| Method                              | What it returns                            | When to override                                                           |
| ----------------------------------- | ------------------------------------------ | -------------------------------------------------------------------------- |
| `getComputedSortFieldExpressions()` | `Map<String, ComputedSortField>`           | String columns (case-insensitive), array columns, computed/fallback fields |
| `getSortFieldAliases()`             | `Map<String, String>` field → aliased path | Nested object columns, companion numeric fields                            |

### Optional

| Method                                      | When to override                                          |
| ------------------------------------------- | --------------------------------------------------------- |
| `buildSearchCriteria(field, trimmedSearch)` | Custom search logic (e.g. fallback field, multi-field OR) |

---

## Sort column patterns

Choose the right hook for each column type. Using the wrong hook produces silent misbehavior (sort operates on the wrong value, or null/empty rows float to the top).

### 1. Plain scalar string column

Use `getComputedSortFieldExpressions()` with `toLowerExpr()` for case-insensitive sort. Dot-path notation works for nested fields:

```java
"model_type", ComputedSortField.of(toLowerExpr("model_type")),
"name",       ComputedSortField.of(toLowerExpr("name.link_text"))  // nested child field
```

### 2. Array column

Use `getComputedSortFieldExpressions()` with `arrayToLoweredStringExpr()`. This reduces the array to a NUL-separated lowercase string so `$sort` produces a stable, human-readable order. Do NOT use `getSortFieldAliases()` for array fields.

```java
"nominating_teams", ComputedSortField.of(arrayToLoweredStringExpr("nominating_teams"))
```

### 3. Nested object column (heatmap cell, time-point bucket)

Use `getSortFieldAliases()` to redirect `$sort` to the numeric sub-field. Without the alias, `$sort` operates on the full object and produces undefined ordering.

The alias must match a scalar sub-field, not a nested object. These keys **must stay in sync with the document schema** -- if a new object-valued column is added to the OpenAPI spec and document class, add the corresponding alias here.

```java
// DiseaseCorrelation: each brain region is { correlation, adj_p_val }
Map.entry("CBE",  "CBE.correlation"),
Map.entry("DLPFC","DLPFC.correlation"),
...

// Transcriptomics: each time-point is { log2_fc, adj_p_val }
"4 months",  "4 months.log2_fc",
"12 months", "12 months.log2_fc",
```

### 4. Companion numeric field

Use `getSortFieldAliases()` to redirect from the display field to its numeric counterpart.

```java
"age", "age_numeric"
```

### 5. Computed fallback field

Use `getComputedSortFieldExpressions()` with a prerequisite `$addFields` stage that computes the value before sort. Wire the prerequisite via `ComputedSortField.of(...).withPrerequisite(op)`.

Also add the field to `getSortFieldAliases()` so the isEmpty flag checks the computed value (not the raw field). Without the alias, rows with a blank primary field but a populated fallback would incorrectly sort to the tail.

```java
// In getComputedSortFieldExpressions():
GENE_SYMBOL_FIELD,
ComputedSortField.of(toLowerExpr(DISPLAY_GENE_SYMBOL_FIELD))
  .withPrerequisite(buildDisplayGeneSymbolField())

// In getSortFieldAliases():
GENE_SYMBOL_FIELD, DISPLAY_GENE_SYMBOL_FIELD
```

---

## Overriding search: buildSearchCriteria

The default search (driven by `searchFilter` in `CtFilterConfig`) supports two modes: comma-separated exact match (case-insensitive) and single-term partial regex. Override `buildSearchCriteria(String field, String trimmedSearch)` when that isn't enough.

**Critical constraint:** the count query uses `mongoTemplate.count()`, which does NOT run the aggregation pipeline. Computed `$addFields` values (like `display_gene_symbol`) are unavailable in the count query. Any override must replicate fallback logic using raw document fields only:

```java
@Override
protected Criteria buildSearchCriteria(String field, String trimmedSearch) {
  // Can't use display_gene_symbol here -- count query bypasses the pipeline.
  // Instead, match gene_symbol directly OR (gene_symbol blank AND ensembl_gene_id matches).
  Criteria geneSymbolBlank = new Criteria()
    .orOperator(
      Criteria.where(GENE_SYMBOL_FIELD).is(null),
      Criteria.where(GENE_SYMBOL_FIELD).is(""),
      Criteria.where(GENE_SYMBOL_FIELD).regex("^\\s*$")
    );
  // ... build match branches and combine with orOperator
}

```

---

## Spaced field names

MongoDB's `"$field"` expression syntax silently fails for field names that contain spaces (e.g. `"4 months"`). `ApiHelper.buildIsEmptyExpr()` detects spaces and uses `$getField` + `$let` automatically. No special handling is needed in the subclass, but be aware:

- **Spaced path, one dot** (`"4 months.log2_fc"`) -- supported.
- **Spaced path, two or more dots** -- throws `IllegalArgumentException` at runtime. Use an alias to a single-dot path instead.
- **Space in `$addFields` key** -- unreliable in DocumentDB. `isEmptyFlagKey()` normalises spaces to underscores automatically.

---

## CtFilterConfig builder

```java
CtFilterConfig.<MyQueryDto>builder()
  .dataFilter("mongo_field", MyQueryDto::getField)   // multi-value $in/$nin filter
  .simpleItemFilter("name")                           // single-field item include/exclude
  .compositeItemFilter(s -> MyIdentifier.parse(s).toCriteria()) // multi-field item filter
  .searchFilter("search_field")                       // free-text search
  .build();
```

- Use `simpleItemFilter` when each item string _is_ the field value (e.g. item `"APOE"` matches `name = "APOE"` directly).
- Use `compositeItemFilter` when each item string is a compound key encoding multiple fields (e.g. `"APOE~Hippocampus~Female"` needs to be split and matched across `gene`, `tissue`, `sex`). Parse it with an identifier DTO whose `toCriteria()` returns an AND-clause; the base class combines them with `$or` / `$nor` for include/exclude.
- `searchFilter` drives `buildSearchCriteria()`; override that method if the default (comma-separated exact match OR single-term partial regex) is insufficient.

---

## Base criteria injection

When the collection is always scoped by a request parameter (cluster, tissue, sex_cohort), pass the mandatory `Criteria` objects as varargs at the end of `buildCtMatchCriteria()`:

```java
Criteria matchCriteria = buildCtMatchCriteria(
  query,
  items,
  isInclude,
  query.getSearch(),
  getFilterConfig(),
  Criteria.where("tissue").is(tissue),
  Criteria.where("sex_cohort").is(sexCohort)
);

```

---

## Existing implementations (reference)

| App      | Class                                    | Collection            | Notable                                                                                                          |
| -------- | ---------------------------------------- | --------------------- | ---------------------------------------------------------------------------------------------------------------- |
| Agora    | `CustomNominatedTargetRepositoryImpl`    | `nominatedtargets`    | 4 array columns, simple item filter                                                                              |
| Agora    | `CustomNominatedDrugRepositoryImpl`      | `nominateddrugs`      | 2 array columns, composite item filter                                                                           |
| Model-AD | `CustomModelOverviewRepositoryImpl`      | `model_overview`      | 1 array column                                                                                                   |
| Model-AD | `CustomDiseaseCorrelationRepositoryImpl` | `disease_correlation` | Nested object columns (brain regions), companion numeric field, base criteria (cluster)                          |
| Model-AD | `CustomTranscriptomicsRepositoryImpl`    | `rna_de_aggregate`    | Nested object columns (time-points), computed fallback field, custom search, base criteria (tissue + sex_cohort) |

All implementations are under `apps/<product>/api-next/src/main/java/.../model/repository/`.
