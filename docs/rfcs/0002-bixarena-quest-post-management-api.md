---
title: BixArena Quest Post Management API
date: 2026-03-11
status: open for review
author: tschaffter
reviewers: []
discussion: # Add GitHub PR URL after opening feedback PR
---

# BixArena Quest Post Management API

## Metadata

- **Status**: Open for Review
- **Author**: [tschaffter](https://github.com/tschaffter)
- **Created**: 2026-03-11
- **Last Updated**: 2026-03-11

## Summary

Move the BixArena Community Quest configuration from hardcoded Gradio constants to a
database-backed API, enabling admins to publish new quest posts (chapters, announcements, lore
entries, etc.) without redeploying the application. Posts support scheduled publishing, progress-gated
unlocking, and tier-based access control.

## Motivation

### Problem Statement

The Community Quest content is defined in a `QUEST_CONFIG` dictionary in the Gradio application
(`bixarena_quest_section.py`). This creates two problems:

1. **Redeployment for content updates**: Every time a new post is published — typically weekly — the
   application must be redeployed. This creates unnecessary engineering overhead and deployment risk
   for what is fundamentally a content update.
2. **No content gating**: There is no way to lock post content behind quest progress milestones or
   contributor tiers. All content is either visible to everyone or not present at all. This prevents
   us from rewarding active contributors with exclusive content (e.g., lore, behind-the-scenes
   material) or creating community-wide milestones that unlock new posts as the quest progresses.
3. **No content exclusivity**: Post content is committed to the public GitHub repository and
   visible to anyone browsing the source code or PR diffs. This makes it impossible to offer
   truly exclusive content, even with client-side gating.

### Current State

- **Quest metadata** (title, description, goal, dates) is hardcoded in `QUEST_CONFIG`.
- **Posts** (chapters, announcements) are hardcoded in `QUEST_CONFIG["updates"]` as a list of
  dictionaries with `date`, `title`, `description`, and `images` fields.
- **Tier configuration** (colors, emojis, thresholds) is hardcoded in `TIER_CONFIG`.
- **Credits** (designer, architect attribution) are hardcoded in `QUEST_CONFIG`.
- **The only API-backed data** is quest contributor stats via `GET /quests/{questId}/contributors`.
- Publishing a new post requires a code change, PR review, merge, and deployment.
- **Post content is publicly visible on GitHub** because it is committed to the repository.

### Desired State

- Admins can create, update, reorder, and delete quest posts via API calls (using `curl` with a
  `JSESSIONID`).
- Posts can be scheduled for future publication, gated behind quest progress milestones, and
  restricted to specific contributor tiers.
- The Gradio app fetches the full quest configuration (metadata + posts) from the API on each page
  load, eliminating redeployments for content updates.
- Exclusive content is stored in the database, not in the public GitHub repository.
- Tier configuration, conversion text, and credits remain in Gradio as they are stable UI concerns
  that rarely change.

## Proposal

### Overview

Extend the existing `quest` database table with metadata fields and introduce a new `quest_post`
table. Expose a public read endpoint for the Gradio app and admin write endpoints for content
management. Each post has three independent unlock gates: publish date, progress threshold, and
contributor tier.

### Data Model

The quest configuration is split into two entities:

**Quest** — extends the existing `quest` table with metadata fields that are currently hardcoded:
title, description, goal (target total battle count), and active post index (which post accordion
to expand by default). The API response also includes the current total block count (computed
server-side), so Gradio can render the progress bar without summing contributor battle counts
client-side.

**Quest Post** — a new entity representing a single piece of quest content. Each post belongs to a
quest and has:

- **Display fields** (always visible): title, optional date, display ordering index
- **Content fields** (protected by unlock gates): description text and image URLs
- **Unlock gates** (see below): publish date, required progress, required tier

### Post Unlock Model

Each post has three independent gates. All must be satisfied for content to be visible:

| Gate              | Description                                                             |
| ----------------- | ----------------------------------------------------------------------- |
| **Publish gate**  | Post is hidden entirely until the publish date is reached               |
| **Progress gate** | Content is locked until the quest's total battle count reaches a target |
| **Tier gate**     | Content is locked unless the caller has a sufficient contributor tier   |

The gates are orthogonal and can be combined freely. For example, an admin can schedule a post for
next Monday that only Champions can see once the quest reaches 2,000 battles.

**Visibility rules:**

- **Unpublished posts** (before publish date) are excluded entirely from the API response. This
  prevents spoiling future content, including titles.
- **Published but locked posts** (progress or tier gate not met) return metadata (title, date,
  required tier, required progress) but **not** the description or images. This allows the Gradio
  app to render locked-state placeholders (e.g., "Reach 1,000 blocks to unlock" or "Become a Knight
  to access this post").
- **Unlocked posts** return the full content.

**Tier hierarchy:** `champion > knight > public`. A higher tier grants access to all lower-tier
content. The API backend already computes contributor tiers in `QuestService.java` using the
existing `CHAMPION_THRESHOLD` and `KNIGHT_THRESHOLD` constants, so the new endpoint reuses this
logic with no duplication.

### API Surface

**Public endpoint** (anonymous, tier-aware when authenticated):

- A read endpoint to fetch the full quest configuration (metadata + published posts) for a given
  quest ID. Content is filtered based on the caller's tier and quest progress. The Gradio app calls
  this on each page load to replace the hardcoded `QUEST_CONFIG`.

**Admin endpoints** (authenticated, requires admin role):

- Create, update, and delete quests
- Create, update, and delete individual posts
- Reorder all posts via a bulk operation that accepts the complete ordered list of post indexes,
  avoiding the index-shifting problems of single-move operations

Admin endpoints return all posts including unpublished ones, with full content regardless of unlock
gates. This allows admins to verify scheduled posts and review locked content.

It is acceptable for admins to manage content using `curl` commands with a `JSESSIONID`. No admin
UI is required.

### What Stays in Gradio

These items remain hardcoded in the Gradio application as they are stable UI concerns:

- **Tier configuration** (`TIER_CONFIG`): colors, emojis, display thresholds
- **Conversion text**: "1 Battle = 1 Block" and its description
- **Credits**: designer and architect attribution
- **Carousel rotation interval**: pure UI setting
- **Feature flag** (`COMMUNITY_QUEST_ENABLED`): environment variable
- **Locked post placeholder text**: Gradio renders this using the required tier and required
  progress values from the API response

### Key Design Decisions

- **Posts, not chapters**: The term "post" is generic enough to encompass chapters, announcements,
  lore entries, patch notes, and other content types without implying a sequential narrative.
- **Three independent unlock gates**: Publish date, required progress, and required tier are
  orthogonal. This avoids complex conditional logic and lets admins combine gates freely.
- **Unpublished posts hidden entirely**: Posts before their publish date are excluded from API
  responses (not just locked). This prevents spoiling future content titles.
- **Locked posts show metadata**: Title, date, required tier, and required progress are always
  returned for published posts, enabling the Gradio app to render teaser/placeholder UI.
- **Bulk reorder over single-move**: A single endpoint that accepts the full ordering avoids
  index-shifting issues when chaining multiple move operations.
- **Admin via curl**: No admin UI is required. The team is small and technical; `curl` commands are
  sufficient. A future admin dashboard (if needed) can consume the same API endpoints.
- **Server-side content filtering**: Tier and progress gating must be enforced at the API layer to
  prevent content leaking. Client-side gating is insufficient since the data would be visible in the
  page source or network traffic.

### Implementation Phases

#### Phase 1: Database and OpenAPI Specification

- Database migration to extend the quest table and create the quest post table
- Define new schemas and endpoints in the OpenAPI spec
- Generate API clients and server stubs
- Mock data migration for development

#### Phase 2: API Service Implementation

- Implement the public read endpoint with unlock gate logic and tier resolution
- Implement admin CRUD endpoints for quest metadata and posts
- Implement the reorder endpoint

#### Phase 3: Content Migration and Gradio Integration

- Manually migrate existing posts from `QUEST_CONFIG` to the database using admin `curl` commands
- Replace `QUEST_CONFIG` with a fetch to the new quest endpoint
- Update the quest section UI to handle locked post states using API response fields
- Remove hardcoded post data from the codebase

## Alternatives Considered

### Alternative 1: Store posts in a CMS (e.g., Contentful, Strapi)

**Pros**:

- Rich content editing UI out of the box
- Image hosting and optimization built in
- Non-technical users can manage content

**Cons**:

- Introduces a new external dependency and service cost
- Requires integration with the existing auth system for tier-based access control
- Overkill for the current content volume and team size

**Decision**: Rejected. Neither CMS has knowledge of BixArena users, tiers, or battle counts, so
the Java API would still need to proxy requests and apply content filtering — making the CMS a
glorified content store behind the API. The team is small and technical; `curl` commands are
sufficient. A CMS adds complexity without proportional value.

### Alternative 2: Store posts as static JSON in S3

**Pros**:

- Simple to implement
- No database changes needed
- Could use CloudFront for caching

**Cons**:

- No access control at the data layer — tier gating would need to be enforced entirely in Gradio
- No transactional guarantees for reordering
- Splits data across two storage systems (S3 for posts, RDS for quest metadata)

**Decision**: Rejected. Tier-based access control and progress gating are better enforced at the API
layer with database-backed state.

### Alternative 3: Keep posts hardcoded, automate deployment

**Pros**:

- No API changes needed
- Could use a GitHub Actions workflow triggered by merging a post file

**Cons**:

- Still requires a PR and deployment per post
- No runtime access control (tier/progress gating)
- Doesn't support scheduled publishing without additional CI complexity
- Content remains publicly visible on GitHub

**Decision**: Rejected. Doesn't address the core needs for runtime content management, access
control, and content exclusivity.

## Security Considerations

- Admin endpoints require authentication and admin role authorization.
- Tier-based content filtering is enforced server-side. The API never returns protected content to
  unauthorized callers.
- Image URLs are the only user-provided URLs stored. They point to GitHub raw content and are
  rendered in Gradio with its built-in sanitization.
- Post descriptions and titles should be validated for length. Image arrays should be validated to
  contain only valid URLs.

## Performance Implications

- The public read endpoint adds one database query per page load. The quest and posts tables are
  small (single quest, ~10-20 posts), so this is negligible.
- The endpoint also needs the total battle count for progress gating, reusing the existing query
  from the contributors endpoint.
- For authenticated callers, an additional indexed query resolves the user's tier from their battle
  count. The required index already exists from the V6 migration.

## Success Criteria

- [ ] Admins can publish a new quest post via a single `curl` command without any deployment
- [ ] Posts with a future publish date are not visible to any user
- [ ] Posts with a progress gate show locked state until the quest reaches the threshold
- [ ] Posts with a tier gate show locked state for users below the required tier
- [ ] The Gradio app renders the quest section entirely from API data (no hardcoded posts)
- [ ] Post reordering works correctly via the bulk reorder endpoint

## Open Questions

None at this time.

## References

- Current quest configuration: `apps/bixarena/app/bixarena_app/page/bixarena_quest_section.py`
- Existing quest API: `libs/bixarena/api-description/src/paths/quests/{questId}/contributors.yaml`
- Quest database migration: `apps/bixarena/api/src/main/resources/db/migration/common/V6__create_quest_table.sql`
