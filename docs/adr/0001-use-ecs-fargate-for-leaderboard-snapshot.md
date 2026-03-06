# 0001: Use ECS Scheduled Fargate Task for Leaderboard Snapshot Generation

- **Status**: accepted
- **Date**: 2026-03-06
- **Decision Makers**: [tschaffter](https://github.com/tschaffter), [rrchai](https://github.com/rrchai)
- **Tags**: infrastructure, bixarena, leaderboard

## Context

RFC-0001 proposed using AWS Lambda (`DockerImageFunction`) triggered by EventBridge to run the leaderboard snapshot container daily. During implementation, a blocking constraint was discovered: **AWS Lambda only supports container images stored in Amazon ECR**. It cannot pull images from external registries such as GHCR (GitHub Container Registry).

All bixarena services publish container images exclusively to GHCR for stage and prod deployments. Adding an ECR push step to CI would require maintaining a second registry, additional CI complexity, and diverge from the established monorepo pattern used by every other service (`api-service`, `auth-service`, `web`, `app`, `ai-service`). While Lambda could be used in dev with a locally built image pushed to ECR, this would create an inconsistency where dev and stage/prod use different compute primitives.

## Decision

We will use `ScheduledFargateTask` (ECS Fargate) instead of `DockerImageFunction` (AWS Lambda) for the leaderboard snapshot scheduled execution.

## Rationale

- **Registry compatibility**: Fargate accepts any OCI-compliant registry via `ecs.ContainerImage.from_registry()`, including GHCR. No registry migration or duplication needed.
- **Consistency**: All other bixarena services run on ECS Fargate using the same cluster, VPC, and CDK patterns. Reusing `ScheduledFargateTask` keeps the stack uniform and avoids introducing a new compute primitive.
- **Simpler credential injection**: ECS injects secrets from Secrets Manager at task start via `ecs.Secret.from_secrets_manager()` — the same mechanism used by `api-service` and `auth-service`. No boto3 call needed in the handler.
- **No execution time limit**: Lambda has a 15-minute hard cap. Fargate has no equivalent constraint, providing headroom if bootstrap iterations or data volume grow.
- **Acceptable trade-offs**: The main costs of Fargate vs Lambda for this workload — longer cold start (~30–60s vs ~1–3s) and per-second billing — are negligible for a once-daily background job where nobody is waiting on the result.

## Consequences

### Positive

- GHCR image publishing works without any registry changes.
- CDK stack follows the same patterns as all other bixarena stacks (no new IAM primitives, no Lambda-specific packaging).
- Credential injection is handled by the ECS agent — handler code has no AWS SDK dependency.
- No 15-minute execution limit.

### Negative

- Cold start is ~30–60 seconds (Fargate task spin-up) vs ~1–3 seconds for Lambda.
- Manual invocation uses `aws ecs run-task` (async, no return value) rather than `aws lambda invoke` (synchronous, returns response).
- `ScheduledFargateTask` does not expose a `.service` property, so CloudFormation outputs for the task are not straightforward to add.
- GuardDuty automatically injects a sidecar container into Fargate tasks; its ECR pull requires explicit `ecr:GetAuthorizationToken` permissions on the task execution role (not needed for Lambda).

### Neutral

- Handler entrypoint changed from `lambda_handler(event, context)` to `run()` + `if __name__ == "__main__"` — standard Python script pattern.
- Schedule source changed from `aws_events.Schedule.cron()` to `aws_applicationautoscaling.Schedule.cron()`.
- The project and container remain named `bixarena-lambda` — the name reflects the workload's nature (short-lived task, not a long-running service), not the AWS compute service used.

## Alternatives Considered

### Option 1: AWS Lambda with ECR push in CI

Push the `bixarena-lambda` image to ECR in addition to GHCR on every CI build.

**Rejected because**: Introduces a second registry to maintain, adds CI complexity, and diverges from the single-registry pattern used by all other bixarena services.

### Option 2: AWS Lambda with zip packaging

Package the handler and dependencies as a zip file instead of a container image, avoiding the ECR requirement.

**Rejected because**: NumPy and SciPy require native binaries compiled for the Lambda execution environment. Zip packaging with native deps requires cross-compilation tooling and is significantly more complex than the container workflow already used by the rest of the stack.

## Related Decisions

- [RFC-0001](../rfcs/0001-bixarena-leaderboard-snapshot-automation.md): Source RFC proposing Lambda-based automation
- [Architecture Plan](../architecture/bixarena-leaderboard-snapshot-automation-plan.md): Full implementation details including divergence table
