---
name: ct-repository
description: >
  Reference guide for implementing and configuring Comparison Tool (CT) repository classes
  that extend ComparisonToolRepositorySupport. Use this skill whenever the user is: creating
  a new CT repository, adding a sort column to an existing CT, debugging unexpected sort order
  (null rows floating up, wrong column ordering), configuring filters or search for a CT,
  reviewing a CT repository implementation, or asking how to wire up any part of the CT
  backend pipeline (sort hooks, filter config, base criteria, spaced field names, heatmap
  columns, array columns, computed/fallback fields). Trigger phrases include "new CT",
  "comparison tool", "CT repository", "CT sort", "null rows", "empty rows", "sort order",
  "heatmap column sort", "array column sort", "getComputedSortFieldExpressions",
  "getSortFieldAliases", "ComparisonToolRepositorySupport", "CtFilterConfig".
---

Read and apply the rule at [`.claude/rules/ct-repository.md`](../../rules/ct-repository.md). If that path isn't found, search for `ct-repository.md` under `.claude/rules/` from the repo root.

That rule is the single source of truth for the CT repository pattern. It covers:

- The full pipeline shape and which stages are optional
- All five sort column patterns (scalar string, array, nested object, companion numeric, computed fallback) and which hook to use for each
- Spaced field name constraints and what `ApiHelper` handles automatically
- `CtFilterConfig` builder reference (dataFilter, simpleItemFilter, compositeItemFilter, searchFilter)
- Base criteria injection for collection-scoped queries
- A reference table of all existing implementations to use as examples
