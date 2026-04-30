---
name: workflow-upgrade-eval
description: Evaluates a proposed version bump for a GitHub Action, reusable workflow, or composite action by comparing the upstream changelog against how the action is actually used in this repo. Use this skill whenever the user mentions bumping, upgrading, updating, or evaluating a version change for anything referenced via `uses:` in `.github/workflows/` *or* `.github/actions/` -- including phrases like "should we upgrade X", "is it safe to bump Y from A to B", "evaluate breaking changes for Z", "Dependabot opened a PR for...", or "what changes between v4 and v7 of...". Trigger even when the user doesn't explicitly say "evaluate" -- if they're asking about the impact of a workflow dependency version change, run this.
---

# Workflow Upgrade Evaluator

## Purpose

Tell the user, with confidence, whether a proposed version bump for a workflow dependency is safe to merge. "Safe" has two dimensions:

1. **Compatibility:** does anything break? Compare upstream changes (breaking changes, new defaults, removed inputs, runtime requirements) against what this repo actually uses.
2. **Security:** does the bump change the trust boundary, credential handling, or attack surface? Even a fully compatible bump can shift security posture (new transitive deps, changed token handling, different default secret exposure).

A change that's "breaking" in the abstract may be a non-issue here. A change that's "non-breaking" upstream can still introduce a security regression in this repo if it touches a sensitive call site (e.g. `pull_request_target`, jobs with elevated `permissions:`, or steps that handle secrets). The skill's job is to bridge upstream changes and local usage so the user gets a grounded answer on both axes.

## When to use

Trigger on requests like:

- "Can you evaluate bumping `actions/checkout` from v3 to v5?"
- "We're considering upgrading `peter-evans/create-pull-request`. Any breaking changes?"
- "Dependabot opened a PR bumping `actions/upload-artifact` to v7 -- safe to merge?"
- "What changed between v1 and v2 of our reusable workflow `Sage-Bionetworks/foo/.github/workflows/bar.yml`?"

Covers three reference shapes, found in both `.github/workflows/*.{yml,yaml}` and `.github/actions/**/action.{yml,yaml}` (local composite actions can themselves `uses:` upstream actions, so a bump may need to be applied inside a composite action too):

- **Marketplace actions**: `uses: owner/action@<ref>`
- **Reusable workflows**: `uses: owner/repo/.github/workflows/file.yml@<ref>`
- **Composite/local actions**: `uses: ./.github/actions/<name>` (no version, but still benefits from a usage audit when the action is edited)

## Workflow

### 1. Confirm inputs

You need three things. If any are missing, ask:

- The action/workflow reference (e.g., `peter-evans/create-pull-request`)
- The current version (e.g., `7.0.11`)
- The target version (e.g., `8.1.1`)

If the user gives only the action and says "latest," ask what version they're targeting. Don't guess -- the wrong version makes the whole report wrong.

### 2. Run the two investigations in parallel

These are independent -- always launch them in the same turn so they overlap.

**A. Local usage audit (Grep + Read).** Find every place the action is referenced and capture the inputs being passed. The key data to extract for each call site:

- File path and line number
- The exact pinned ref (SHA + version comment)
- The `with:` inputs being passed (names only is enough; values matter only if relevant to a known breaking change)
- **Security-relevant context for the surrounding job/workflow:**
  - The workflow trigger (`on:`) -- flag `pull_request_target`, `workflow_run`, `issue_comment`, and any trigger that runs with the _base_ repo's secrets against fork-controlled code
  - The job's `permissions:` block (or workflow-level default)
  - Whether the job runs inside a `container:` (affects runtime/glibc compatibility for new Node versions)
  - Whether secrets (`secrets.*`, `${{ secrets.GITHUB_TOKEN }}`) are passed as inputs, env vars, or referenced in nearby steps
  - Defensive flags already set: `persist-credentials: false`, explicit `ref:` to PR HEAD, etc.
  - Whether self-hosted runners are used (`runs-on:` not starting with `ubuntu-`/`windows-`/`macos-`)

Use `Grep` with the action name across both `.github/workflows/` _and_ `.github/actions/` (e.g. `Grep` with no path filter, then narrow to those two directories), then `Read` each hit with enough context (10-30 lines after the match) to capture the `with:` block. Read the workflow's `on:` block too -- it's usually at the top of the file.

For hits inside `.github/actions/<name>/action.yml`, the surrounding context is different: there's no `on:` trigger and no job-level `permissions:` -- the security posture is inherited from every workflow that calls the composite action. Note this in the report and, if any caller workflow uses a sensitive trigger (`pull_request_target`, etc.), surface that connection explicitly.

**Verify the current pin against the canonical tag ref.** For each call site, the workflow pins a SHA with a `# v<old>` comment. Resolve what the canonical tag actually points to and compare:

```bash
gh api repos/<owner>/<repo>/git/ref/tags/v<old> --jq '.object.sha'
```

If the returned SHA matches the pinned SHA, the existing pin is intact -- record this in the report. If they diverge, halt the evaluation and flag it explicitly: this means either the version comment is wrong (typo, drift), the tag was rewritten upstream after pinning (annotated-vs-lightweight tag mismatch is a benign cause; a hijacked tag is the dangerous one), or a previous bump landed a pin that didn't match its claimed version. Don't proceed with a bump recommendation until the divergence is understood.

**B. Upstream changelog research.** Goal: collect the release notes for every release between `<old>` and `<new>` (inclusive of intermediate majors), plus any migration docs, then synthesize the breaking changes.

**Preferred: `gh` CLI.** It's already authenticated in this devcontainer and gives structured JSON, which is faster and more reliable than HTML scraping. Probe first:

```bash
gh auth status >/dev/null 2>&1 && echo "gh ready" || echo "gh unavailable"
```

If `gh` is ready, enumerate and fetch in one shot:

```bash
# List releases (most recent first; bump --limit if the gap is wider)
gh release list --repo <owner>/<repo> --limit 30 \
  --json tagName,name,publishedAt,isPrerelease

# For each release between <old> and <new>, fetch the body
gh release view <tag> --repo <owner>/<repo> \
  --json tagName,name,body,publishedAt
```

Read the JSON output directly -- there's no need to spawn a subagent for fetching. Then read `README.md` and any `UPGRADING.md` / `MIGRATION.md` via `gh api repos/<owner>/<repo>/contents/<path>` or a quick `WebFetch` to the raw URL.

Also enumerate any security advisories filed against versions in this range:

```bash
gh api repos/<owner>/<repo>/security-advisories \
  --jq '.[] | {ghsa_id, summary, severity, published_at, vulnerabilities}'
```

If the API returns nothing or 404s (advisories disabled), fall back to a quick `WebFetch` of `https://github.com/<owner>/<repo>/security/advisories`.

**Compute release age** from the `publishedAt` field on the target release. If the target version is less than **3 days old**, flag it -- a freshly-cut release hasn't had time for the community to spot a malicious or accidental bad publish (compromised maintainer token, supply-chain injection, etc.). The skill should _not_ silently approve a < 3-day-old version. Surface the age in the Security implications section and recommend either waiting until the release ages past 3 days or, if the user wants to proceed anyway, calling the risk out explicitly. This applies to the target version specifically; intermediate releases in the changelog don't matter for this check.

**Fallback: subagent with web fetching.** If `gh` is unavailable or unauthenticated, spawn a `general-purpose` agent. Be explicit about coverage so it doesn't skip intermediate majors and so it surfaces security-relevant changes:

```
Research the breaking changes, API changes, and security-relevant changes in <action>
between version <old> and <new>.

1. Check https://github.com/<owner>/<repo>/releases for every release between <old> and <new>
   (including all intermediate major versions).
2. Check the README/UPGRADING/MIGRATION/SECURITY docs and any security advisories
   (https://github.com/<owner>/<repo>/security/advisories).
3. Identify breaking changes in inputs, outputs, defaults, runtime, and permissions.
4. Identify security-relevant changes:
   - Credential / secret handling (where tokens are stored, logged, or exposed)
   - Default values for security-sensitive inputs (e.g. persist-credentials, set-safe-directory)
   - New transitive dependencies or runtime privileges
   - Changes to what the action writes to disk, env, or stdout
   - Changes to publisher / maintainer / signing
   - Any CVEs or security advisories filed against versions in this range
5. Report the publish date of the target version (`<new>`) so age can be computed against the current date. Flag if < 3 days old.

Report all of the above plus migration steps. Be thorough -- this is for evaluating whether we
can safely bump, both for compatibility and security posture.
```

**In either path, the synthesis is the same.** Identify: breaking changes, new/removed/renamed inputs, output changes, default behavior changes, runtime requirements (Node version, runner version), permission changes, **and security-relevant changes**. Cover every intermediate major version, not just the target.

### 3. Cross-reference and write the report

Once both investigations return, intersect them on **two axes**:

- **Compatibility:** for each breaking change, does any call site trip over it?
- **Security:** for each security-relevant change, does any sensitive call site (privileged trigger, elevated `permissions:`, secret-handling, self-hosted runner) shift its posture?

Both must be addressed explicitly. A bump that's compatible but introduces a security regression is _not_ safe; a bump that's security-neutral but breaks a call site is _not_ safe either.

Use this exact structure:

```markdown
## Evaluation Summary

**The bump from <old> to <new> is <safe / risky / blocked>.**

- **Compatibility:** <no breakage / breaks call site X / requires migration>
- **Security:** <no change / improvement / regression / blocked>

### Current usage (<N> call sites)

| File | Line | Trigger                                         | Inputs used | Sensitive context                                                                                         |
| ---- | ---- | ----------------------------------------------- | ----------- | --------------------------------------------------------------------------------------------------------- |
| ...  | ...  | `push` / `pull_request` / `pull_request_target` | ...         | none / `pull_request_target` + secrets / elevated `permissions:` / containerized job / self-hosted runner |

### SHA pin transition

| Action / workflow / composite | Current pin                     | Proposed pin                    |
| ----------------------------- | ------------------------------- | ------------------------------- |
| `<owner>/<action>`            | `<short-sha-old>...` (`v<old>`) | `<short-sha-new>...` (`v<new>`) |

Both pins verified against canonical tag refs via `gh api repos/<owner>/<repo>/git/ref/tags/<tag>`. <Note any divergence found, or "no divergence" if both match.>

### Breaking changes <by version if multiple majors crossed>

| Version  | Breaking change | Impact on this repo      |
| -------- | --------------- | ------------------------ |
| v<X>.0.0 | <change>        | <None / specific impact> |

### Security implications

For each security-relevant change identified upstream, note whether it improves, regresses, or leaves posture unchanged in this repo. Cover at minimum:

- **Credential / secret handling** -- where the action stores/logs/exposes tokens; whether anything in this repo depends on the prior location.
- **Sensitive trigger interactions** -- if any call site uses `pull_request_target`, `workflow_run`, or similar, does the bump change what fork-controlled code can influence?
- **Permissions / token scope** -- does the new version need `permissions:` adjustments?
- **Supply chain** -- publisher unchanged? Any CVEs or advisories filed against versions in this range? SHA-pinning still effective?
- **Release age** -- how old is the target release (`publishedAt` from `gh release view`)? Flag if < 3 days; recommend waiting through the community quarantine window unless the user has reason to bump immediately.
- **Runtime / container compatibility** -- if the bump raises Node version and any call site runs inside a `container:`, flag glibc/musl compatibility (Node 24 needs glibc >= 2.28).
- **Self-hosted runner exposure** -- new runner version requirements, new disk/network behavior.

If a category is genuinely N/A, say so explicitly -- the user needs to see what was checked. To avoid bloating easy bumps, you can collapse multiple clearly-N/A categories into a single line (e.g. "N/A: `pull_request_target` / containers / self-hosted runners -- none in use"). Never silently skip a category.

### What hasn't changed

- **Inputs you use** (`<list>`) -- identical across versions.
- **Outputs** -- unchanged.
- **Permissions** -- no changes required.
- **Default behavior** -- <note any defaults that matter>.

### Migration steps

<Numbered list of concrete actions, or "Just update the SHA pin" if that's all.>

### One-sentence summary

<A single sentence the user can paste into a PR description or Slack message. State the verdict on both compatibility and security.>
```

#### Calibrating the verdict

If a breaking change _does_ affect a call site, say so plainly -- don't soften the verdict. For a risky bump:

- Flag the specific call site(s) affected
- Quote the exact behavior change
- Recommend either: (a) a code change before bumping, (b) skipping this version, or (c) accepting the risk with mitigation

If self-hosted runners are in play and the new version raises the minimum runner version, flag it -- this repo uses `ubuntu-latest` for most workflows, but verify rather than assume.

### 4. Offer to apply the bump

After delivering the report, ask whether to update the SHA pin. Don't update preemptively. The pinned SHA for the new version comes from the GitHub release page or `git ls-remote https://github.com/<owner>/<repo> refs/tags/v<new>`.

## Examples of good output

**Low-risk bump** (the common case):

> The only breaking change is the Node.js 20 -> 24 runtime bump (requiring self-hosted runners v2.327.1+), which doesn't affect this repo since it uses GitHub-hosted `ubuntu-latest` runners, and all inputs, outputs, and permissions are identical between v7 and v8.

**Multi-major bump** (cover every intermediate version):

> The three major version bumps are internal dependency upgrades (v5), a Node.js 24 runtime requirement that only affects self-hosted runners (v6), and an ESM migration with a new optional `archive` input that defaults to preserving existing behavior (v7) -- none of the inputs, outputs, or defaults you rely on have changed.

The one-sentence summary should make the verdict obvious in a glance: state what changed, why it does or doesn't affect this repo, and whether it's safe.

## Watch-outs

### Compatibility

- **Don't skip intermediate majors.** Bumping v4 -> v7 means reading the v5 _and_ v6 _and_ v7 release notes. Breaking changes accumulate; the v7 release notes won't restate what broke in v5.
- **Don't trust the action description.** The README often lags releases. The release notes are authoritative.
- **Pinned SHAs hide the version.** Workflows in this repo pin to a SHA with a `# v7.0.11` comment. The comment is the source of truth for "current version" -- the SHA alone tells you nothing without resolving it.
- **Companion actions drift.** If `actions/upload-artifact` is bumped, check whether `actions/download-artifact` should move in lockstep -- they share a server-side protocol and version mismatches can break artifact retrieval. Mention this when relevant, but don't auto-expand scope unless the user asks.

### Security

- **Verify the SHA, don't trust the tag.** When you produce the new pinned SHA, get it from `gh api repos/<owner>/<repo>/git/ref/tags/v<new>` (or `git ls-remote`) -- a hijacked tag is the classic supply-chain vector that SHA pinning defends against. Note in the report that the SHA was resolved from the canonical tag ref.
- **`pull_request_target` and friends are the danger zone.** If a call site uses `pull_request_target`, `workflow_run`, or `issue_comment`, the workflow runs with the _base_ repo's secrets while potentially executing fork-controlled code. Any bump that changes how the action handles inputs, secrets, or `ref:` deserves extra scrutiny here. Existing mitigations (`persist-credentials: false`, explicit PR-HEAD `ref:`) only hold if the bump preserves them.
- **Default value changes are silent regressions.** A "non-breaking" bump that flips a default (e.g. `persist-credentials` from `true` to `false`, or vice versa) won't appear in any code change but can break or expose downstream steps. Diff the defaults explicitly.
- **Containerized jobs and Node version bumps.** When a call site runs inside a job-level `container:`, the runner mounts its bundled Node into the container. A Node-version bump (e.g. 20 -> 24) requires the container's glibc to be compatible (Node 24 needs glibc >= 2.28). Flag this for any containerized call site.
- **Credential storage location changes are usually security improvements -- but verify.** Actions moving secrets out of `.git/config` or workspace files into `$RUNNER_TEMP` are net positive; just confirm nothing in the repo (artifact uploads, debug `git config --list` calls, custom credential extraction) relied on the old location.
- **Forks and re-publishes.** If the action is _not_ published by a well-known org (GitHub itself, large vendors), check the publisher hasn't changed and there are no open security advisories. Mention publisher reputation explicitly when the action is third-party.
- **Quarantine fresh releases.** Don't recommend bumping to a release that's < 3 days old. The 3-day window is when compromised maintainer tokens, malicious tag pushes, or accidental bad publishes are most likely to be caught and yanked by the community. The age check uses `publishedAt` from `gh release view`. If the user has a strong reason to bump immediately (e.g. urgent CVE fix in the new release), say so explicitly and call out the heightened risk -- but don't silently approve a freshly-cut release.
