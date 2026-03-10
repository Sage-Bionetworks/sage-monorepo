# 0001: Use ECS Scheduled Fargate Task for Leaderboard Snapshot Generation

- **Status**: accepted
- **Date**: 2026-03-06
- **Decision Makers**: [rrchai](https://github.com/rrchai)
- **Tags**: infrastructure, bixarena, leaderboard

## Context

RFC-0001 proposed using AWS Lambda (`DockerImageFunction`) triggered by EventBridge to run the leaderboard snapshot container daily. During implementation, a blocking constraint was discovered: **AWS Lambda only supports container images stored in Amazon ECR**. It cannot pull images from external registries such as GHCR (GitHub Container Registry).

For dev, this is not an issue — `image_loader.py` uploads a local tarball to ECR as a CDK asset, which Lambda can pull. For stage and prod, all bixarena services publish to GHCR only, so Lambda has no compatible image source without an additional sync step.

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
- The project and container are named `bixarena-worker`, reflecting the compute primitive used.
- Fargate bills per-second while the task runs; Lambda bills per-100ms. For a once-daily short-lived job, the cost difference between the two is negligible.

## Alternatives Considered

### Option 1: AWS Lambda with GHCR→ECR image sync at deploy time

Sync the GHCR image to ECR as part of the CDK deployment pipeline so Lambda's `DockerImageFunction` can pull from ECR while CI continues publishing to GHCR only.

**Rejected because**: Adds deployment complexity — `image_loader.py` would need a third code path to sync and resolve the ECR URI for Lambda while other services continue using GHCR directly. `ScheduledFargateTask` avoids this entirely by accepting GHCR URIs natively, reusing the existing VPC, cluster, and CDK secrets patterns without any additional infrastructure or registry management.

## Related Decisions

- [RFC-0001](../rfcs/0001-bixarena-leaderboard-snapshot-automation-plan.md): Source RFC proposing Lambda-based automation
- [Architecture Plan](../architecture/bixarena-leaderboard-snapshot-automation-plan.md): Full implementation details including divergence table
