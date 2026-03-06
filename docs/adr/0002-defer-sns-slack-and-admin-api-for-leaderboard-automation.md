# 0002: Defer SNS/Slack Notifications and Admin API Endpoint for Leaderboard Automation

- **Status**: accepted
- **Date**: 2026-03-06
- **Decision Makers**: [rrchai](https://github.com/rrchai)
- **Tags**: infrastructure, bixarena, leaderboard, monitoring

## Context

RFC-0001 and the architecture plan included two operational features beyond the core scheduled job:

1. **SNS + Slack notifications** — alert on task success/failure via CloudWatch → SNS → Slack.
2. **Admin API endpoint** — `POST /admin/generate-snapshot` in the AI service, allowing on-demand
   snapshot generation via a protected HTTP endpoint (JWT-gated, returns 202 + correlation ID).

The core automation (Phases 1–3: shared library, container, scheduled Fargate task) is implemented
and running successfully. The question is whether to implement these two supporting features now or
defer them.

## Decision

We will defer both features indefinitely and revisit them only when a concrete need arises:

- **SNS/Slack notifications**: Deferred. A future admin dashboard will surface task status and
  history, making push notifications to Slack redundant.
- **Admin API endpoint**: Deferred. Developers can manually trigger a snapshot using
  `nx run bixarena-fargate:invoke`. A future admin dashboard UI will expose on-demand triggering
  for non-developer admins.

## Rationale

- **Notification fatigue**: Daily Slack pings for a background job add noise without actionable
  value. Failures are visible in CloudWatch logs; a dashboard is a better long-term surface.
- **Admin dashboard planned**: The admin dashboard (future work) will cover both monitoring
  (task run history, success/failure) and manual triggering. Building a separate API endpoint
  now creates parallel surfaces that will need to be consolidated later.
- **Nx invoke sufficient for now**: Developers can trigger snapshot generation locally via
  `nx run bixarena-fargate:invoke` without any additional infrastructure.
- **Scope control**: Keeping v1 focused on the scheduled job reduces CDK complexity, avoids
  new IAM primitives (SNS publish, Lambda invoke from AI service), and removes the need for
  `boto3` in the AI service.

## Manual Trigger

Developers can generate a prod snapshot on demand by opening an SSH tunnel to the prod RDS
instance and invoking the Lambda handler locally:

```bash
# 1. Open SSH tunnel to prod RDS
nx run bixarena-infra-cdk:start-db-tunnel prod

# 2. Configure prod DB credentials in apps/bixarena/fargate/.env
#    (copy from .env.example and fill in POSTGRES_HOST, POSTGRES_USER, POSTGRES_PASSWORD, etc.)

# 3. Run the handler locally
nx run bixarena-fargate:invoke
```

This runs `uv run python -m leaderboard_snapshot.handler` in the Fargate project directory,
executing the same snapshot generation and publish logic used by the scheduled task.

## Consequences

### Positive

- No additional CDK resources (SNS topic, IAM policies, AI service `boto3` dependency).
- Simpler operational surface — one scheduler, one log group, one task family.
- Admin dashboard will be a better, unified interface for both monitoring and triggering.

### Negative

- No automated alerting if the daily scheduled task fails silently. Failures must be discovered
  by actively checking CloudWatch logs or the leaderboard UI.
- Manual trigger via `nx run bixarena-fargate:invoke` requires developer access (local environment
  with DB connectivity) — not accessible to non-developer admins.

### Neutral

- CloudWatch Logs remain the primary observability surface for task execution.
- The architecture plan is updated to mark these items as "Deferred (future admin dashboard)".

## Alternatives Considered

### Option 1: Implement SNS + Slack now

Add a CloudWatch alarm on task failure → SNS topic → Slack webhook subscription.

**Deferred because**: Daily noise for a background job; admin dashboard is the better long-term
surface for operational status.

### Option 2: Implement admin API endpoint now (Phase 4)

Add `POST /admin/generate-snapshot` to the AI service with JWT auth, invoking `ecs run-task`
via `boto3`.

**Deferred because**: Developers can use `nx run bixarena-fargate:invoke` directly; the endpoint
will be better surfaced through the future admin dashboard UI rather than a standalone API.

## Related Decisions

- [ADR-0001](./0001-use-ecs-fargate-for-leaderboard-snapshot.md): Fargate vs Lambda decision
- [RFC-0001](../rfcs/0001-bixarena-leaderboard-snapshot-automation.md): Source RFC
- [Architecture Plan](../architecture/bixarena-leaderboard-snapshot-automation-plan.md): Full implementation details
