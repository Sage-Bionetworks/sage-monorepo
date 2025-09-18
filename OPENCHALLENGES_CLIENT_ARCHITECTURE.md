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

Implemented (challenge gateway only, current): exponential backoff with jitter for `{429,500,502,503,504}` up to `retries` attempts per _page request_ (configurable). Formula: `sleep = 2^attempt + random(0,1)`, capped at 30s.

Planned: extend to organization gateway; add configurable base / max delay, and optional logging.

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

Commands:

```
openchallenges challenges list [--status STATUS ...] [--limit N] [--output table|json]
openchallenges orgs list [--search TEXT] [--limit N] [--output table|json]
```

Global flags supply config fallback; output uses Rich tables or JSON. Planned: YAML, config diagnostics.

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
- [ ] Retry/backoff (organization gateway)
- [ ] CLI: `config show` / diagnostics
- [ ] Enhanced auth error hint (missing key guidance)

### Phase 3 – Experience Enhancements

- [ ] Streaming iterators (unbounded fetch with backpressure / user cap)
- [ ] YAML output (optional dependency)
- [ ] Richer domain models (platform slug, incentives, categories)
- [ ] Normalized date handling & human-readable durations
- [ ] Organization search fallback suggestions (zero results)
- [ ] Friendly truncation notice when limit < total available
- [ ] Pluggable output format registry

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

---

## 13. Regeneration Checklist (Generated Client)

1. Regenerate via OpenAPI Generator (matching current schema).
2. Re-apply platform tolerance patch in `challenge.py` (search `CUSTOM PATCH`).
3. Run unit tests and smoke CLI command.
4. Verify status enum names unchanged (adjust gateway mapping if needed).
5. Update version & changelog if external surface affected.

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

Core discovery flows working. Reliability features (pagination, retries for challenges, error mapping, file-based config) implemented. Remaining work focuses on parity (organization retries), richer experience features, and broader test coverage.

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
