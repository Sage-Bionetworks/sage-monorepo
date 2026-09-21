# Comparison Tool Repository Pattern

This rule covers every task that touches a `ComparisonToolRepositorySupport` subclass: creating a new CT repository, adding or debugging a sort column, declaring a parent/child hierarchy, reviewing an implementation, or understanding why rows are ordered or truncated unexpectedly.

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
    return executePagedAggregation(matchCriteria, pageable, isInclude, query.getRemainingBudget());
  }
}

```

Key points:

- `@Repository @Slf4j` on the class; constructor takes only `MongoTemplate`.
- Always `Objects.requireNonNullElse(query.getItemFilterType(), ItemFilterTypeQueryDto.INCLUDE)` -- the frontend may send null and the base class does not default it.
- `findAll` signature is defined by the custom interface, not the base class -- add product-specific parameters (tissue, cluster, etc.) there and pass them as base criteria varargs.
- A parent-aware CT, or one whose caller needs `hasRowsForPrebudgetedParents`, builds a `CtQueryOptions` instead of the bare `isInclude` / `remainingBudget` pair and passes it to both `buildCtMatchCriteria` and `executePagedAggregation`. See **Parent/child CTs**.

## Query parameter wiring

Every field the repository reads off the search query DTO has to be declared in three places outside the repository. Each one fails at runtime rather than at compile time, so none of them is caught by simply getting `findAll` to build:

- **The search query schema** (`libs/<product>/api-description/src/components/schemas/*SearchQuery.yaml`), then `nx run-many -t=generate -p=<product>-*` to regenerate the DTO and clients.
- **The delegate's `VALID_QUERY_PARAMS`** (`apps/<product>/api-next/.../api/*ApiDelegateImpl.java`) -- `ApiHelper.validateQueryParameters` rejects any request carrying an unlisted parameter with a 400. These sets mirror the schema's property order, not alphabetical order.
- **The service's `@Cacheable` key**, built via `ApiHelper.buildCacheKey(...)` -- a field missing from the key makes two requests that differ only by that field collide on one cache entry.

The parent/child fields are no exception: `itemIdSpace` and `prebudgetedParentIds` each need all three declarations, and their `$ref`ed enum (`ItemIdSpaceQuery.yaml`) needs a converter entry in `EnumConverterConfiguration`. The response half, `hasRowsForPrebudgetedParents`, is a nullable property on the `*Page.yaml` schema that the service copies off the returned `CtPage`; a repository whose custom interface still declares `Page<T>` compiles fine and silently loses the flag, so narrow the interface to `CtPage<T>` when a caller needs it. What these fields do is covered under **Parent/child CTs**.

One constraint carries over from search. The existence query behind `hasRowsForPrebudgetedParents` uses `mongoTemplate.exists`, which -- like the `count()` documented under **Overriding search** -- bypasses the aggregation pipeline and therefore reads **stored fields only**. A parent identity space that depends on a computed `$addFields` value cannot answer it.

## Pipeline shape

The base class assembles this pipeline on every paged request:

```
$match          ← assembled from getFilterConfig() + optional base criteria
[$addFields]    ← prerequisites for computed sort fields (when needed)
[$addFields]    ← computed sort fields from getComputedSortFieldExpressions()
[$addFields]    ← isEmpty flags (null/empty rows → tail), always present when sorted
$sort           ← field names resolved via aliases and computed fields
[$skip / $limit] ← a bare $limit when the request caps rows; neither when it caps parents
```

Stages in brackets are omitted when not needed (e.g. no computed sort field requested, or sort is unsorted). The `allowDiskUse: true` option and `collation: {locale: "en", strength: 2}` (case-insensitive) are set on every CT aggregation. The tail is a bare `$limit` instead of `$skip` / `$limit` when the request caps returned rows via `remainingBudget` -- subclasses just forward it, so see the `executePagedAggregation` javadoc for when it applies.

A budgeted request on a parent-aware CT spends its budget on parents rather than rows, which changes both ends of the shape: the `$match` is narrowed to the admitted parents' rows, and the tail carries no `$skip` and no `$limit`, since every row of an admitted parent is wanted. A subclass forwards its `CtQueryOptions` and implements none of it -- see **Parent/child CTs**.

## Hooks to override

### Required

| Method                | What it returns                       | When to override |
| --------------------- | ------------------------------------- | ---------------- |
| `getCollectionName()` | MongoDB collection name string        | Always           |
| `getDocumentClass()`  | Java POJO class                       | Always           |
| `getFilterConfig()`   | `CtFilterConfig<Q>` built via builder | Always           |

### Sort configuration

| Method                              | What it returns                            | When to override                                                      |
| ----------------------------------- | ------------------------------------------ | --------------------------------------------------------------------- |
| `getComputedSortFieldExpressions()` | `Map<String, ComputedSortField>`           | Array columns, computed/fallback fields                               |
| `getSortFieldAliases()`             | `Map<String, String>` field → aliased path | Nested string fields, nested object columns, companion numeric fields |

### Optional

| Method                                      | When to override                                                |
| ------------------------------------------- | --------------------------------------------------------------- |
| `buildSearchCriteria(field, trimmedSearch)` | Custom search logic (e.g. fallback field, multi-field OR)       |
| `getParentIdSpace()`                        | Several rows roll up to one parent -- makes the CT parent-aware |
| `getRowIdSpace()`                           | Rarely; the default derives row identity from the item filter   |

---

## Sort column patterns

Choose the right hook for each column type. Using the wrong hook produces silent misbehavior (sort operates on the wrong value, or null/empty rows float to the top).

### 1. Plain scalar string column

No hook needed -- pipeline-level collation (`strength: 2`) makes all string sorts case-insensitive automatically. For nested fields (e.g. `name.link_text`), use `getSortFieldAliases()` to redirect to the child path:

```java
// In getSortFieldAliases():
"name", "name.link_text"
```

### 2. Array column

Use `getComputedSortFieldExpressions()` with `arrayToStringExpr()`. This reduces the array to a NUL-separated string so `$sort` produces a stable, human-readable order. Case-insensitive comparison is handled by pipeline-level collation. Do NOT use `getSortFieldAliases()` for array fields.

```java
"nominating_teams", ComputedSortField.of(arrayToStringExpr("nominating_teams"))
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
ComputedSortField.of("$" + DISPLAY_GENE_SYMBOL_FIELD)
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

## Parent/child CTs

Overriding `getParentIdSpace()` is what makes a CT **parent-aware**: several rows roll up to one parent, so a request can match `items` against the parent token instead of the row token, and an EXCLUDE budget caps distinct parents rather than rows. Leave it null and the CT stays **self-parented** -- the parent space resolves back to `getRowIdSpace()`, every row is its own parent, and no other behaviour changes. Most CTs need no override.

### Declaring an identity space

An `ItemIdSpaceDef` (`libs/explorers/api-helper/`) does two things: it turns a client-supplied item token back into `Criteria` over stored fields, and it rebuilds that same token inside the aggregation, which is what lets rows be grouped by parent. No token is stored in MongoDB, so both directions are derived per request.

| Factory                                                                           | Use when                                               | Must declare                                             |
| --------------------------------------------------------------------------------- | ------------------------------------------------------ | -------------------------------------------------------- |
| `ItemIdSpaceDef.stored("ensembl_gene_id")`                                        | The token _is_ one stored field's value                | --                                                       |
| `ItemIdSpaceDef.composite(FIELDS, item -> MyIdentifier.parse(item).toCriteria())` | The token encodes several stored fields, joined by `~` | `FIELDS`, even though the parser alone would match items |

- A parent space must be able to emit its token, so it needs the field names. A space derived from `compositeItemFilter` carries a parser and no field names, and `tokenExpression()` throws `IllegalStateException` on it -- harmless for a self-parented CT, which never emits a token, fatal for a parent space, which is grouped on.
- A blank part -- absent, null, empty, or whitespace only -- renders as the literal `"null"` on both sides of the token, so an identifier DTO has to render it identically or the two emitters disagree about the same row. It does not round-trip: a DTO parses it back as a literal string rather than an absent field (MG-586). A token that is malformed rather than blank, such as one whose value contains the delimiter, fails the request through the DTO's own parse rather than being skipped, since such a row is unaddressable by every feature keyed by that token.
- Keep the delimiter and missing-part constants private to the identifier DTO rather than importing `ItemIdSpaceDef` into app code, and assert the two renderings agree in the DTO's **test** (`TranscriptomicsIdentifierTest`) -- api-helper cannot see the DTO and neither side runs Mongo, so that test is the only place a mismatch surfaces.

### The request knobs

The three query fields below reach the repository as one `CtQueryOptions`. Build it in `findAll` and pass the same instance to both `buildCtMatchCriteria` and `executePagedAggregation`, so the two cannot disagree about the request. A CT with no parent-awareness keeps using the `boolean isInclude` / `Integer remainingBudget` overloads.

| Query field            | Effect                                                                                                                                                        |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `itemIdSpace`          | `row` (default) or `parent` -- which space `items` is matched against. Exactly one space is matched, never an `OR` of both                                    |
| `prebudgetedParentIds` | Parents the caller has already accounted for, whose further children are free. Always matched against the parent space, independently of `itemIdSpace`        |
| `remainingBudget`      | How much the caller can still accept: distinct new parents on a parent-aware CT, rows otherwise. EXCLUDE only, and `pageNumber` / `pageSize` are then ignored |

### Budgeting parents

A budget caps parents only on an EXCLUDE request against a parent-aware CT: it admits the prebudgeted parents plus up to `remainingBudget` more in the request's own sort order, and every row of each comes back unpaged, however many pages it spans. A budget of zero admits no new parent, on a self-parented CT as much as on a parent-aware one -- nothing is selected at zero, so both kinds admit the prebudgeted parents alone; an exhausted budget with no prebudgeted parents matches nothing rather than everything; a null one with prebudgeted parents selects no parents at all, and only drives `hasRowsForPrebudgetedParents`. The total count still reflects the **unnarrowed** match set, so a caller detects truncation exactly as in the row-capped case.

### Frontend contract

A hierarchy is declared per view rather than computed: the keys below are stored on the view's config document, read by the product's `ComparisonToolConfigDocument`, and served by the comparison-tool-config endpoint onto the frontend's `ComparisonToolConfig` (`libs/explorers/models/`).

| Config key                 | Meaning                                                                                                                                             |
| -------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| `row_id_data_key`          | The data key holding this view's row UID, overriding the comparison tool's default row id key for the view                                          |
| `parent_id_data_key`       | The data key holding the id of a row's parent. Equal to `row_id_data_key` means rows are their own parents; a different key means rows are children |
| `parent_noun`, `view_noun` | Singular/plural nouns labelling parent counts and row counts. A child view sets both, since it displays both counts                                 |

All four are nullable, and a view that declares no hierarchy leaves them unset. `parent_id_data_key` must resolve to the same parent values in every view of a comparison tool, though not necessarily through the same field, since different views read different collections.

**TODO (MG-1095):** the component wiring belongs here -- how pinned-item state turns these config keys into `itemIdSpace` and `prebudgetedParentIds` on a request, and how the comparison table consumes `hasRowsForPrebudgetedParents`. Extend this subsection rather than starting a second account of the mechanism elsewhere.

---

## Base criteria injection

When the collection is always scoped by a request parameter (cluster, tissue), pass the mandatory `Criteria` objects as varargs at the end of `buildCtMatchCriteria()`:

```java
Criteria matchCriteria = buildCtMatchCriteria(
  query,
  items,
  isInclude,
  query.getSearch(),
  getFilterConfig(),
  Criteria.where("tissue").is(tissue)
);

```

---

## Existing implementations (reference)

| App      | Class                                    | Collection             | Notable                                                                                                                     |
| -------- | ---------------------------------------- | ---------------------- | --------------------------------------------------------------------------------------------------------------------------- |
| Agora    | `CustomNominatedTargetRepositoryImpl`    | `nominatedtargets`     | 4 array columns, simple item filter                                                                                         |
| Agora    | `CustomNominatedDrugRepositoryImpl`      | `nominateddrugs`       | 2 array columns, composite item filter                                                                                      |
| Model-AD | `CustomModelOverviewRepositoryImpl`      | `model_overview`       | 1 array column                                                                                                              |
| Model-AD | `CustomDiseaseCorrelationRepositoryImpl` | `disease_correlation`  | Nested object columns (brain regions), companion numeric field, base criteria (cluster)                                     |
| Model-AD | `CustomTranscriptomicsRepositoryImpl`    | `rna_de_aggregate`     | Nested object columns (time-points), computed fallback field, custom search, base criteria (tissue), self-parented          |
| Model-AD | `CustomProteomicsRepositoryImpl`         | `protein_de_aggregate` | Nested object columns (time-points), composite item filter, custom multi-field search, base criteria (tissue), parent-aware |

All implementations are under `apps/<product>/api-next/src/main/java/.../model/repository/`.

For a worked parent-aware example, read Proteomics: its protein isoform rows roll up to the gene their transcriptomics counterpart is keyed by, so it declares its parent space from `TranscriptomicsIdentifier.FIELDS` -- the token the RNA view identifies its own rows with. Every other CT is self-parented, which takes no override.
