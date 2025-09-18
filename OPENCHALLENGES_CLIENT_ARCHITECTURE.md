# OpenChallenges Python Client & CLI Architecture

> Revision: 2025-09-18  
> Scope: `libs/openchallenges/client-python` – high‑level experiential client & CLI over the generated OpenAPI SDK (`libs/openchallenges/api-client-python`).

---

## 1. Purpose & Vision

Provide a concise, user‑friendly Python interface and command line tool to discover, explore, and eventually contribute to OpenChallenges resources. It abstracts the low‑level generated SDK behind stable, human‑centric operations (Experience API pattern): _list challenges_, _list organizations_, (future) _search_, _details_, _stats_.

### Core Principles

| Principle               | Description                                                             |
| ----------------------- | ----------------------------------------------------------------------- |
| Simplicity              | One intuitive method/command per journey (not raw endpoints).           |
| Stability               | Shield users from breaking Swagger/SDK changes.                         |
| Progressive Enhancement | Start read‑only & expand (filters, retries, richer models).             |
| Observability           | Predictable errors, distinct exit codes, optional retries.              |
| Layered Isolation       | Generated SDK confined to gateways; domain & services remain decoupled. |

### Non‑Goals (Current Phase)

- Full CRUD / write operations
- Advanced analytics or caching
- Multi-threaded prefetching
- Full schema mirroring of every field

---

## 2. User Journeys (Initial + Near Term)

1. Discover top challenges quickly (default limit).
2. Filter challenges by status (e.g., ACTIVE, COMPLETED).
3. Find organizations by text search.
4. Script automation (JSON output) for downstream tooling.
5. (Planned) Stream all results without manual paging.
6. (Planned) Inspect effective configuration & provenance.

---

## 3. High-Level Architecture

```
CLI (Typer)  ───────────────┐
Library Facade (OpenChallengesClient) ────────┐
                                             │
                Service Layer (Use Cases) ───┤  list_challenges / list_organizations
                                             │
                Gateways (API Adapters) ─────┤  pagination, retries, enum normalization
                                             │
      Generated OpenAPI Client (pydantic v2) ┤  auto-generated models, serialization
                                             │
                   HTTP OpenChallenges REST ─┘
```

### Layer Responsibilities

| Layer                           | Responsibility                                                      | Knows About              |
| ------------------------------- | ------------------------------------------------------------------- | ------------------------ |
| CLI                             | UX, parsing flags, output format, exit codes                        | Facade only              |
| Facade (`OpenChallengesClient`) | Stable API surface                                                  | Services & config        |
| Services                        | Orchestrate use case, enforce limits                                | Gateways & domain models |
| Gateways                        | Translate service intent → generated client calls (paging, retries) | Generated SDK, config    |
| Generated Client                | Raw HTTP, serialization                                             | HTTP                     |
| Domain Models                   | Minimal normalized output objects                                   | None (pure data)         |

---

## 4. Configuration Strategy

Precedence (highest → lowest):

1. CLI flags (`--api-url`, `--api-key`, `--limit`)
2. Environment variables (`OC_API_URL`, `OC_API_KEY`, `OC_RETRIES`)
3. Nearest `.openchallenges.toml` (walk upward from CWD) then `$HOME/.openchallenges.toml`  
   Section `[openchallenges]` or flat root keys accepted.
4. Built‑in defaults (URL, limit=5, retries=0)

Config object: `ClientConfig(api_url, api_key, default_limit, retries)`

---

## 5. Pagination & Limits

Service limit parameter restricts total yielded items across pages. Gateways request page slices using API’s `pageNumber` + `pageSize` while never exceeding the user’s effective limit. Page size currently capped at 100.

---

## 6. Retry / Backoff

Implemented (challenge & organization gateways): exponential backoff with jitter for `{429,500,502,503,504}` up to `retries` attempts per _page request_ (configurable). Formula: `sleep = 2^attempt + random(0,1)`, capped at 30s.

Next (future): configurable base/max delay, optional verbose logging of retry attempts, metrics hook for retry counts.

---

## 7. Error Handling & Exit Codes

Gateway maps `ApiException.status` → domain errors (`AuthError`, `NotFoundError`, `RateLimitError`, etc.). CLI converts these to exit codes:

| Exit | Error Class             |
| ---- | ----------------------- |
| 1    | Generic / unknown       |
| 2    | AuthError               |
| 3    | NotFoundError           |
| 4    | RateLimitError          |
| 5    | ClientError (4xx other) |
| 6    | ServerError (5xx)       |
| 7    | NetworkError            |

---

## 8. Domain Models (Current Simplified Surface)

`ChallengeSummary` & `OrganizationSummary` retain only fields necessary for initial discovery views. Additional attributes (incentives, categories, etc.) deferred until user story demands.

Future enrichment: add platform slug, derived computed fields (duration, is_active), safe enum wrappers.

---

## 9. CLI Design

Commands (current):

```
openchallenges challenges list   [--status STATUS ...] [--limit N] [--output table|json]
openchallenges challenges stream [--status STATUS ...] [--limit N] [--output table|json]
openchallenges orgs list         [--search TEXT]       [--limit N] [--output table|json]
openchallenges orgs stream       [--search TEXT]       [--limit N] [--output table|json]
openchallenges config show       [--output table|json]
```

Global flags supply config fallback; output uses Rich tables or JSON. Planned: YAML output, live progressive rendering & NDJSON optional mode.

---

## 10. Generated Model Patch Note

`Challenge.from_dict` patched to tolerate _empty_ `platform` objects (all fields null) to prevent Pydantic validation errors. Regeneration of the SDK should re‑apply this patch (search for `CUSTOM PATCH`).

---

## 11. Implementation Plan & Checklist

### Phase 1 – MVP (Discovery)

- [x] Project scaffold (package, script entry point)
- [x] Basic domain models (challenge/org summaries)
- [x] Service layer (list challenges / list organizations)
- [x] Gateways with single page fetch → upgraded to pagination
- [x] Config loader (env & overrides)
- [x] CLI with table + JSON output
- [x] Basic unit tests (service limit behavior)

### Phase 2 – Robustness & Filters

- [x] Pagination loops for both gateways
- [x] Status filter (challenges)
- [x] Organization search parameter
- [x] Error mapping & domain exception hierarchy
- [x] Exit codes in CLI
- [x] Config file support (.openchallenges.toml, directory ascent)
- [x] Status enum normalization (challenge gateway)
- [x] Retry/backoff (challenge gateway)
- [x] Tolerate empty platform objects
- [x] Retry/backoff (organization gateway)
- [x] CLI: `config show` / diagnostics
- [x] Enhanced auth error hint (missing key guidance)

### Phase 3 – Experience Enhancements

- [x] Streaming iterators (unbounded fetch via generator + CLI stream command)
- [x] YAML output (optional dependency)
- [ ] Richer domain models (platform slug, incentives, categories)
- [ ] Normalized date handling & human-readable durations
- [ ] Organization search fallback suggestions (zero results)
- [ ] Friendly truncation notice when limit < total available
- [x] Pluggable output format registry
- [ ] Data anomaly handling (sanitized over-length organization logins w/ warning)

### Phase 4 – Quality & Observability

- [ ] Comprehensive pagination tests (multi-page stubs)
- [ ] Retry logic tests (inject transient failures)
- [ ] CLI tests via Typer `CliRunner`
- [ ] Logging (structured, opt-in verbosity flag)
- [ ] Metrics hooks (timings, retry counts)
- [ ] Type & style coverage (ruff additions / mypy optional later)

### Phase 5 – Distribution & Governance

- [ ] Versioning strategy (CalVer or SemVer) & changelog
- [ ] Package metadata finalization (classifiers, license)
- [ ] Publishing pipeline (PyPI / internal registry)
- [ ] Documentation site integration (link from docs portal)
- [ ] Contribution guidelines (coding conventions map to layers)
- [ ] Automated regeneration process notes for SDK

### Backlog / Stretch

- [ ] Caching strategy (in-memory page cache)
- [ ] Concurrency for fast large listings (async or thread pool)
- [ ] Pluggable authentication providers (token refresh, keyring)
- [ ] Offline snapshot export/import
- [ ] Performance benchmarks suite

---

## 12. Risks & Mitigations

| Risk                              | Impact             | Mitigation                                                              |
| --------------------------------- | ------------------ | ----------------------------------------------------------------------- |
| SDK regeneration breaks patch     | Runtime errors     | Keep a minimal, clearly marked patch block; add regeneration checklist. |
| Expanding model surface too early | Maintenance load   | Enforce story-driven enrichment.                                        |
| Silent status filter mismatches   | Confusing results  | (Planned) warn when all provided statuses are invalid.                  |
| Hidden retries cause latency      | Perceived slowness | Add verbose flag & retry summary lines.                                 |
| Data anomalies (invalid field lengths) | Partial failures / user confusion | Gateway-level soft sanitation with optional strict mode & warnings. |

---

## 13. Regeneration Checklist (Generated Client)

1. Regenerate via OpenAPI Generator (matching current schema).
2. Re-apply platform tolerance patch in `challenge.py` (search `CUSTOM PATCH`).
3. Run unit tests and smoke CLI command.
4. Verify status enum names unchanged (adjust gateway mapping if needed).
5. Update version & changelog if external surface affected.
6. Scan for over-length organization logins; decide: fix data vs. evolve spec. (Do not silently relax model.)

---

## 14. Quick Start (Local)

```
# From repo root (ensure uv environment prepared)
uv run openchallenges challenges list --limit 3
uv run openchallenges challenges list --status ACTIVE --status COMPLETED --output json
uv run openchallenges orgs list --search data --limit 5

# With retries and config file
export OC_RETRIES=3
uv run openchallenges challenges list --status ACTIVE
```

Sample `.openchallenges.toml`:

```
[openchallenges]
api_url = "https://api.openchallenges.io/api/v1"
api_key = "YOUR_KEY_HERE"
default_limit = 10
retries = 2
```

---

## 15. Future CLI Additions (Design Sketch)

```
openchallenges config show          # display resolved config + precedence source
openchallenges challenges stream    # continuous / full iterator (with progress)
openchallenges challenges export --format yaml --out challenges.yaml
```

---

## 16. Current Status Snapshot (2025-09-18)

Core discovery flows working. Reliability parity achieved (retries in both gateways, status normalization, auth hint). Streaming iterators & CLI `stream` commands delivered (buffered display for now). Config diagnostics (`config show`) implemented. YAML & pluggable output registry completed. Upcoming focus: anomaly handling (over-length logins), enhanced domain enrichment, pagination / retry test coverage, and progressive/live streaming refinements.

---

## 20. Data Anomaly Handling Strategy (Read Path)

### Context
Occasionally backend data may drift outside the published OpenAPI contract (e.g., an organization `login` exceeding the 64 character max). Failing the entire listing due to a single malformed row harms usability; silently relaxing the model hides real data quality issues.

### Goals
1. Preserve strict schema validation (contract integrity).
2. Avoid breaking bulk read operations when a minority of rows are invalid.
3. Provide visibility so anomalies are corrected upstream.
4. Allow callers to opt into strict failure for CI / data quality enforcement.

### Approach
Layer a defensive parsing step in the `OrganizationGateway`:

1. Fetch page via generated SDK (raw dicts before model instantiation).
2. For each raw org:
   - Attempt normal model validation.
   - If validation passes → yield.
   - If it fails ONLY due to an over-length `login`:
     - Produce a sanitized clone with `login` truncated to 64 chars (no suffix to avoid new overflow risk).
     - Record anomaly metadata (org id and original length) in a per-call warning accumulator.
     - Yield the sanitized model to keep iteration flowing.
   - If validation fails for any other reason → raise mapped domain error (fail fast: indicates broader schema drift).
3. After yielding all rows for the call, expose accumulated anomalies:
   - Library: attach a lightweight `context` object or expose via a method (e.g., `gateway.last_warnings`).
   - CLI: if any anomalies and not in quiet mode, emit: `Warning: 1 organization had an over-length login truncated to 64 chars (IDs: 12345).` Multiple IDs comma-separated.

### CLI Flags (Planned)
| Flag | Behavior |
|------|----------|
| `--strict` | Abort on first anomaly (non-zero exit). |
| `--quiet-anomalies` | Suppress warning emission (default is to show once). |

### Domain Model Metadata (Optional Future)
Introduce a lightweight `meta` dict on summaries when sanitized: `summary.meta = {"sanitized_login": True, "original_login_length": 87}`. Omitted when pristine.

### Rationale
This preserves user scripts (they still see almost all data), highlights quality issues early, and keeps the spec authoritative. Users needing exact raw values can lobby for spec evolution or server-side correction—client does not silently expand the contract.

### Upstream Remediation Workflow
1. Detect warning in CLI / logs.
2. Query backend for offending org id(s).
3. Decide: shorten login in DB vs. adopt spec change (increase max_length) and regenerate SDK + bump version.

### Testing Plan
Add a unit test with a fake gateway returning one valid and one over-length login to ensure:
* Sanitized record is truncated.
* Warning accumulator records anomaly.
* Strict mode raises.

### Non-Goals
* Bulk normalization beyond truncation.
* Automatic server update / mutation.
* Handling of unrelated validation errors (these remain hard failures).

---

---

## 17. How to Resume After a Break

1. Read this document fully (changes since last commit visible in git log).
2. Run a smoke test: `uv run openchallenges challenges list --limit 2`.
3. Pick next unchecked item in Phase 2 or 3 (unless priorities changed).
4. If touching generated models, consult Regeneration Checklist.
5. Add tests when behavior becomes less trivial (pagination, retries).

---

## 18. Appendix – Exit Codes Reference

| Code | Meaning                    |
| ---- | -------------------------- |
| 0    | Success                    |
| 1    | Generic/unclassified error |
| 2    | Authentication failure     |
| 3    | Not found                  |
| 4    | Rate limited               |
| 5    | Client (other 4xx) error   |
| 6    | Server (5xx) error         |
| 7    | Network/transport error    |

---

## 19. Contribution Conventions

- Keep gateway changes minimal & side‑effect free (no business logic).
- Services return iterables/generators; avoid forcing list materialization upstream.
- Add new CLI output modes via helper `_emit` abstraction.
- Prefer additive domain model evolution—avoid breaking existing fields.

---

**End of Document**
