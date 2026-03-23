# Hackathon: "AI Insights" for Agora Pinned Genes

## Context

Sage-wide hackathon (2 hours). Goals:

1. Prototype an AI feature for Agora triggered from **pinned genes on the Gene Comparison Tool page**
2. Showcase **Claude Code Agent Teams** with split-panel tmux recording
3. **Scientist-in-the-loop** to validate and iterate the system prompt

Implementation is **frontend-only** in `apps/agora/app/` — no Java backend changes. LLM calls go directly to OpenRouter from the Angular frontend using a 1-day expiration token.

---

## Part 1: Pre-Hackathon Setup

### 1.1 Permissions Setup for Agent Teams

The teammates need broad permissions to build without constant prompting. Update `.claude/settings.local.json` to allow:

Update `~/.claude/settings.json` to add broad permissions for the hackathon session. The teammates inherit the lead's permissions:

```json
{
  "env": {
    "CLAUDE_CODE_EXPERIMENTAL_AGENT_TEAMS": "1"
  },
  "teammateMode": "tmux",
  "permissions": {
    "allow": [
      "Bash(npx:*)",
      "Bash(nx:*)",
      "Bash(source:*)",
      "Bash(docker:*)",
      "Bash(npm:*)",
      "Bash(node:*)",
      "Bash(curl:*)",
      "Bash(cat:*)",
      "Bash(ls:*)",
      "Bash(cd:*)",
      "Bash(ng:*)",
      "Bash(mkdir:*)",
      "Bash(cp:*)",
      "Bash(mv:*)",
      "Bash(rm:*)",
      "Bash(find:*)",
      "Bash(grep:*)",
      "Bash(head:*)",
      "Bash(tail:*)",
      "Bash(echo:*)",
      "Bash(pwd:*)",
      "Bash(which:*)",
      "Bash(env:*)",
      "Bash(wc:*)",
      "Bash(uv run:*)",
      "Bash(python3:*)",
      "WebFetch(domain:openrouter.ai)",
      "WebSearch"
    ]
  }
}
```

Also update `.claude/settings.local.json` (project-level) to remove overly specific bash permissions and add broad ones:

```json
{
  "permissions": {
    "allow": [
      "Bash(uv run:*)",
      "Bash(python3 -c:*)",
      "Bash(npx:*)",
      "Bash(nx:*)",
      "Bash(docker:*)",
      "Bash(source:*)",
      "Bash(curl:*)"
    ]
  }
}
```

### 1.2 OpenRouter Token Setup

The user will set up a 1-day expiration OpenRouter token. Store it as an environment variable so agents can use it without seeing it:

```bash
# Add to ~/.claude/settings.json env section (user will do this manually)
{
  "env": {
    "OPENROUTER_API_KEY": "sk-or-...",
    "CLAUDE_CODE_EXPERIMENTAL_AGENT_TEAMS": "1"
  }
}
```

The user will manually add the token to `apps/agora/app/src/config/config.json` as `openRouterApiKey` before the hackathon starts. The Angular ConfigService already loads this file. The token expires in 1 day — acceptable for a hackathon prototype.

**Important: No git commits during the hackathon.** All work stays on the current `bixarena/llm-ai-service` branch as uncommitted changes.

### 1.3 tmux Split-Panel Setup

tmux 3.4 is already installed. Set teammate mode to tmux:

```bash
# Add to ~/.claude/settings.json
{
  "teammateMode": "tmux"
}
```

To start the session:

```bash
tmux new-session -s hackathon
# Then inside tmux, launch Claude Code with explicit split-pane mode
claude --teammate-mode tmux
```

Each teammate will get its own tmux pane automatically. Use `Ctrl+B` then arrow keys to switch panes for recording.

### 1.4 Verify Agora Runs

```bash
source dev-env.sh
agora-docker-stop  # stop any existing
agora-build-images
agora-docker-start
# Verify: http://localhost:8000 shows Agora
```

---

## Part 2: Agent Teams Prompt

Paste this one-liner into Claude Code to launch the hackathon team:

```
Read /home/ubuntu/.claude/plans/zippy-bubbling-gadget.md and execute it. Create the agent team described in Part 2. Use tmux split panes — each teammate must run in its own tmux pane.
```

The lead will read the plan, then spawn teammates based on the detailed prompt below (which the lead reads, not you):

```
Hackathon project: Add "AI Insights" to Agora (Alzheimer's Disease gene portal).

Read the full plan first: /home/ubuntu/.claude/plans/zippy-bubbling-gadget.md

The feature: On the Gene Comparison Tool page, scientists pin genes for comparison. We're adding
an "AI Insights" button next to the existing "Download as CSV" and "Clear All" buttons. Clicking
it opens a side panel that sends the pinned genes' data to an LLM via OpenRouter and streams
back an analysis. Users can ask follow-up questions and switch between LLM models.

Constraints:
- Frontend-only — all changes in the Angular app, no Java backend changes
- OpenRouter API key is in apps/agora/app/src/config/config.json as "openRouterApiKey" — read
  via ConfigService, NEVER log/display/echo the key
- NO git commits — all work stays as uncommitted changes
- Hackathon-quality: functional over polished
- Build/run: `source dev-env.sh && agora-docker-stop; agora-build-images && agora-docker-start`
  → app at localhost:8000

Create a team with 4 teammates (use Sonnet for all):

1. **frontend-engineer** — Owns the UI: button, side panel, streaming display, follow-up input.
   Explore the existing GCT component to understand the pinned genes toolbar and data model.
   Use PrimeNG components. Require plan approval before coding.

2. **llm-service-engineer** — Owns the Angular service: fetch full gene data from existing
   Agora API services (GeneService, BioDomainService), build the prompt, call OpenRouter with
   streaming, support model selection (claude-sonnet-4-6, gpt-4o, gemini-2.5-pro,
   llama-4-maverick). The GET /api/v1/genes/{ensg} response includes everything: full gene
   details AND similar_genes_network (coexpressed genes with brain_regions and links). Use
   this to give the LLM context about each pinned gene's neighborhood — so it can compare
   pinned genes against their coexpression neighbors and surface patterns like shared similar
   genes, overlapping brain regions, or pinned genes that appear in each other's networks.
   Make the system prompt easy to modify — a scientist will iterate on it during the hackathon.
   Require plan approval before coding.

3. **ui-designer** — Style the panel, markdown rendering, model selector, conversation thread.
   Ensure visual consistency with existing Agora theme. Review in browser at localhost:8000.

4. **tester** — Wait for initial implementation, then verify end-to-end in browser. Test:
   pin genes → click AI Insights → streaming response, model switching, follow-up questions,
   edge cases (0 genes, 1 gene, many genes). Report bugs with reproduction steps.

Coordination: All teammates must communicate throughout development — share progress, discuss
decisions, and challenge each other's approaches to arrive at the best solution. No teammate
should work in isolation. Message the lead and relevant teammates when completing tasks,
hitting blockers, or making design decisions that affect others.
```

---

## Part 3: What Each Teammate Produces

| Teammate             | Deliverable                                      | Key Files                                                            |
| -------------------- | ------------------------------------------------ | -------------------------------------------------------------------- |
| frontend-engineer    | AI Insights button + panel component             | `gene-comparison-tool.component.{html,ts,scss}`, new panel component |
| llm-service-engineer | OpenRouter streaming service + prompt            | `ai-insights.service.ts`, prompt template                            |
| ui-designer          | Styled panel, markdown rendering, model selector | SCSS files, template tweaks                                          |
| tester               | Bug reports, verification that it works e2e      | Test notes via messages                                              |
| lead                 | Integration, conflict resolution, final build    | Coordinates all                                                      |

---

## Part 4: Scientist Feedback Loop (During Hackathon)

Once the initial version works (~45 min in):

1. Scientist pins genes they care about
2. Reviews AI output → gives feedback on tone, depth, usefulness
3. llm-service-engineer updates the system prompt
4. Repeat 3-5 times over ~30 min
5. Try different models via the selector to compare quality

---

## Part 5: Demo Recording

Split-panel tmux recording showing:

- **Left panes**: Agent teammates building the feature in real-time
- **Right**: Agora browser at localhost:8000 showing the working feature
- Script: Pin APOE + TREM2 + CLU → click AI Insights → streaming analysis → follow-up question → switch model

---

## Key Files Reference

| File                                                                          | Purpose                                                                    |
| ----------------------------------------------------------------------------- | -------------------------------------------------------------------------- |
| `libs/agora/gene-comparison-tool/src/lib/gene-comparison-tool.component.ts`   | Main GCT component (pinned genes state)                                    |
| `libs/agora/gene-comparison-tool/src/lib/gene-comparison-tool.component.html` | GCT template (toolbar at lines ~275-304)                                   |
| `libs/agora/api-client-angular/src/lib/model/gct-gene.ts`                     | GCTGene model interface                                                    |
| `libs/agora/api-client-angular/src/lib/api/gene.service.ts`                   | Existing Gene API service                                                  |
| `libs/agora/api-client-angular/src/lib/api/bio-domain.service.ts`             | Existing BioDomain API service                                             |
| `libs/agora/api-description/openapi/openapi.yaml`                             | API spec                                                                   |
| `apps/agora/app/src/config/config.json`                                       | App config (csrApiUrl, etc.)                                               |
| `dev-env.sh`                                                                  | Shell functions: agora-build-images, agora-docker-start, agora-docker-stop |

## Verification

1. `source dev-env.sh && agora-docker-stop; agora-build-images && agora-docker-start`
2. Open localhost:8000 → Gene Comparison Tool
3. Pin 2-3 genes → "AI Insights" button should appear/be enabled
4. Click → panel opens → model selector visible → LLM streams analysis
5. Type follow-up question → response uses conversation history
6. Switch model → new response from different LLM
