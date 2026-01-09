# Leaderboard Snapshot Automation Implementation Plan

## Overview

Implement automated daily leaderboard snapshot generation using a hybrid approach:

- **Primary**: AWS Lambda function triggered by EventBridge (scheduled)
- **Secondary**: Protected API endpoint in AI service (on-demand)
- **Shared**: Core snapshot generation logic extracted from `bixarena-tools`

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         Scheduled Execution                              │
│                                                                          │
│  EventBridge Rule ──> Lambda Function ──> RDS Proxy ──> PostgreSQL      │
│  (cron: daily 2 AM)   (leaderboard-      (connection   (bixarena DB)   │
│                        snapshot-gen)      pooling)                       │
│                            │                                             │
│                            └──> SNS Topic ──> Slack Lambda ──> Slack    │
│                                 (success/failure notifications)          │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                        On-Demand Execution                               │
│                                                                          │
│  API Gateway ──> AI Service ──> Lambda (async) ──> Returns 202          │
│  (authenticated)  POST /admin/        │              with correlation_id│
│                   generate-           │                                  │
│                   snapshot            └──> RDS Proxy ──> PostgreSQL     │
│                                       └──> SNS ──> Slack notification    │
│                                                                          │
│  Response: {"correlation_id": "...", "status": "initiated"}             │
└─────────────────────────────────────────────────────────────────────────┘

                              ┌──────────────────────────────┐
                              │  Shared Logic                │
                              │  (bixarena-leaderboard)      │
                              │                              │
                              │ - fetch_data()               │
                              │ - compute_bradley_terry()    │
                              │ - save_snapshot()            │
                              │ - auto_publish=True          │
                              └──────────────────────────────┘
```

**Design Notes:**

- Lambda currently generates **"overall"** leaderboard daily
- Extensible design: pass `leaderboard_id` parameter for future leaderboards
- Auto-publish enabled by default (sets `visible=true`)
- Slack notifications sent on both success and failure
- **On-demand execution**: Async Lambda invocation (returns immediately, no timeout issues)
- **Metadata tracking**: `trigger_source` (scheduled/api), `triggered_by` (user email), `correlation_id`

---

## Security Architecture

### Overview

The leaderboard snapshot system implements defense-in-depth security with multiple layers:

1. **Authentication & Authorization**: JWT-based API authentication with admin-only access
2. **IAM Least Privilege**: Lambda and ECS task roles scoped to minimum necessary permissions
3. **Secrets Management**: Database credentials stored in AWS Secrets Manager
4. **Network Isolation**: Lambda in VPC private subnet, RDS Proxy with IAM authentication
5. **Input Validation**: Whitelist-based validation of all Lambda inputs
6. **Audit Logging**: CloudWatch logs with correlation IDs for traceability

### Security Measures by Component

#### 1. Lambda Function Security

**IAM Execution Role** (least privilege):

```python
# Lambda execution role with minimal permissions
lambda_role = iam.Role(
    self, "SnapshotLambdaRole",
    assumed_by=iam.ServicePrincipal("lambda.amazonaws.com"),
    managed_policies=[
        iam.ManagedPolicy.from_aws_managed_policy_name(
            "service-role/AWSLambdaVPCAccessExecutionRole"
        )
    ],
)

# Grant ONLY what's needed:
db_secret.grant_read(lambda_role)  # Read DB credentials
snapshot_events_topic.grant_publish(lambda_role)  # Publish to SNS
rds_proxy.grant_connect(lambda_role, "bixarena_user")  # RDS IAM auth
```

**Network Security** (VPC isolation):

```python
snapshot_function = lambda_.DockerImageFunction(
    self,
    "SnapshotFunction",
    vpc=vpc,
    vpc_subnets=ec2.SubnetSelection(
        subnet_type=ec2.SubnetType.PRIVATE_WITH_EGRESS
    ),
    security_groups=[lambda_security_group],
)

# Security group: outbound to RDS only
lambda_security_group.add_egress_rule(
    peer=ec2.Peer.security_group_id(rds_security_group.security_group_id),
    connection=ec2.Port.tcp(5432),
    description="Allow Lambda to access RDS via proxy"
)
```

**Input Validation**:

```python
def validate_inputs(event: dict) -> None:
    """Validate and sanitize Lambda event inputs."""
    # Whitelist allowed leaderboards
    allowed_leaderboards = ["overall", "open-source", "multimodal"]
    leaderboard_id = event.get("leaderboard_id", "overall")
    if leaderboard_id not in allowed_leaderboards:
        raise ValueError(f"Invalid leaderboard_id: {leaderboard_id}")

    # Validate bootstrap iterations (reasonable range)
    num_bootstrap = event.get("num_bootstrap", 100)
    if not isinstance(num_bootstrap, int) or num_bootstrap < 10 or num_bootstrap > 10000:
        raise ValueError(f"Invalid num_bootstrap: {num_bootstrap}")

    # Validate trigger source
    trigger_source = event.get("trigger_source", "scheduled")
    if trigger_source not in ["scheduled", "api"]:
        raise ValueError(f"Invalid trigger_source: {trigger_source}")
```

#### 2. AI Service API Security

**Authentication** (JWT with admin role):

```python
async def validate_jwt_admin(
    authorization: str = Header(...),
) -> str:
    """Validate JWT and ensure user has admin role."""
    payload = validate_jwt(authorization)  # Verify signature, expiry

    # Check admin role
    roles = payload.get("cognito:groups", [])
    if "admin" not in roles:
        raise HTTPException(
            status_code=403,
            detail="Admin role required for snapshot generation"
        )

    return payload.get("email")  # Return email for audit logging
```

**Lambda Invocation Permission** (scoped IAM):

```python
# AI service ECS task role
ai_service_task_role = iam.Role.from_role_arn(
    self, "AIServiceTaskRole",
    role_arn="arn:aws:iam::123456789012:role/BixArenaAIServiceTaskRole"
)

# Grant permission to invoke ONLY this specific Lambda
snapshot_function.grant_invoke(ai_service_task_role)

# Generated policy:
# {
#   "Effect": "Allow",
#   "Action": "lambda:InvokeFunction",
#   "Resource": "arn:aws:lambda:us-east-1:123456789012:function:snapshot-gen"
# }
```

**Rate Limiting** (API Gateway):

```python
# Prevent abuse of manual snapshot generation
api_gateway.add_usage_plan(
    "SnapshotAPIUsagePlan",
    throttle={
        "rate_limit": 10,  # 10 requests per second
        "burst_limit": 20,
    },
    quota={
        "limit": 100,  # 100 requests per day
        "period": apigateway.Period.DAY,
    },
)
```

#### 3. Database Security

**Secrets Manager** (no hardcoded credentials):

```python
# Database credentials in Secrets Manager
db_secret = secretsmanager.Secret.from_secret_name_v2(
    self,
    "DatabaseSecret",
    secret_name="bixarena/rds/credentials"
)

# Lambda retrieves credentials at runtime
def get_database_url():
    secret_arn = os.environ["DB_SECRET_ARN"]
    client = boto3.client("secretsmanager")
    response = client.get_secret_value(SecretId=secret_arn)
    secret = json.loads(response["SecretString"])

    return (
        f"postgresql://{secret['username']}:{secret['password']}"
        f"@{secret['host']}:{secret['port']}/{secret['dbname']}"
    )
```

**RDS Proxy with IAM Authentication** (no password needed):

```python
rds_proxy = rds.DatabaseProxy(
    self, "RDSProxy",
    proxy_target=rds.ProxyTarget.from_cluster(db_cluster),
    secrets=[db_secret],
    vpc=vpc,
    require_tls=True,  # Enforce TLS encryption
    iam_auth=True,     # IAM database authentication
)

# Lambda connects via IAM (no password in environment)
rds_proxy.grant_connect(snapshot_function, "bixarena_user")
```

#### 4. Audit Logging & Monitoring

**Structured Logging with Metadata**:

```python
logger.info(
    "Snapshot generation started",
    extra={
        "trigger_source": trigger_source,       # scheduled or api
        "triggered_by": triggered_by,           # user email or "system"
        "correlation_id": correlation_id,       # unique trace ID
        "leaderboard_id": leaderboard_id,
    }
)
```

**CloudWatch Alarms**:

```python
# Alert on Lambda errors
error_alarm = cloudwatch.Alarm(
    self,
    "SnapshotErrorAlarm",
    metric=snapshot_function.metric_errors(),
    threshold=1,
    evaluation_periods=1,
    alarm_description="Leaderboard snapshot generation failed",
)
error_alarm.add_alarm_action(cloudwatch_actions.SnsAction(alert_topic))
```

### Security Checklist

- ✅ **IAM**: Least privilege roles for Lambda and ECS tasks
- ✅ **Secrets**: Database credentials in Secrets Manager (or IAM auth)
- ✅ **Network**: Lambda in private VPC subnet with security groups
- ✅ **Input Validation**: Whitelist allowed values, validate types and ranges
- ✅ **Authentication**: API endpoint requires JWT with admin role
- ✅ **Authorization**: ECS task role scoped to invoke only this Lambda
- ✅ **Audit Logging**: CloudWatch logs with trigger_source, triggered_by, correlation_id
- ✅ **Monitoring**: CloudWatch alarms on Lambda errors
- ✅ **Encryption**: TLS for RDS connection, encrypted secrets at rest
- ✅ **Rate Limiting**: API Gateway throttling prevents abuse

---

## Part 1: Lambda Function Packaging Options

### Option A: Container Image (Docker) ⭐ **RECOMMENDED**

**Packaging Approach:**

```dockerfile
# Lambda Dockerfile
FROM public.ecr.aws/lambda/python:3.13

# Copy shared snapshot logic
COPY --from=build /bixarena_snapshot_lib /var/task/

# Copy Lambda handler
COPY lambda_function.py /var/task/

# Set handler
CMD ["lambda_function.handler"]
```

**Pros:**

- ✅ **Full dependency control**: No 250MB deployment package limit
- ✅ **Native dependencies**: NumPy, SciPy compile correctly
- ✅ **Familiar tooling**: Same Docker workflow as other services
- ✅ **Testing**: Run locally with `docker run` or SAM CLI
- ✅ **Reusability**: Base image can be shared across multiple Lambdas
- ✅ **No layer management**: Everything bundled in one artifact

**Cons:**

- ⚠️ **Cold start**: ~2-3 seconds (vs. ~500ms for zip)
- ⚠️ **Image size**: Up to 10GB (vs. 250MB for zip)
- ⚠️ **Build complexity**: Requires Docker build in CI/CD
- ⚠️ **ECR dependency**: Must push to ECR before deployment

**When to Use:**

- Heavy dependencies (NumPy, SciPy, pandas)
- Need native libraries (psycopg binary mode)
- Long-running tasks (snapshot generation takes >30 seconds)
- Complex build requirements

---

### Option B: Zip Archive with Lambda Layers

**Packaging Approach:**

```python
# CDK code
lambda_function = aws_lambda.Function(
    self, "LeaderboardSnapshotFunction",
    runtime=aws_lambda.Runtime.PYTHON_3_13,
    code=aws_lambda.Code.from_asset(
        "../../tools/lambda",
        bundling=BundlingOptions(
            image=Runtime.PYTHON_3_13.bundling_image,
            command=["pip", "install", "-r", "requirements.txt", "-t", "/asset-output"]
        )
    ),
    layers=[numpy_scipy_layer]  # Pre-built layer for heavy deps
)
```

**Pros:**

- ✅ **Fast cold start**: ~500ms with layers
- ✅ **Simpler deployment**: No Docker registry needed
- ✅ **CDK bundling**: Automatic packaging via CDK
- ✅ **Layer caching**: Heavy deps (NumPy/SciPy) cached across functions

**Cons:**

- ⚠️ **Size limits**: 250MB unzipped (50MB zipped)
- ⚠️ **Dependency conflicts**: Layer versions must match
- ⚠️ **Complex bundling**: pip install with --platform linux_x86_64
- ⚠️ **Layer management**: Separate updates for dependencies

**When to Use:**

- Simple dependencies
- Small codebase
- Prioritize cold start performance
- No native compilation requirements

---

### Option C: Zip with Inline Bundling (No Layers)

**Packaging Approach:**

```python
# CDK with asset bundling
lambda_function = aws_lambda.Function(
    self, "LeaderboardSnapshotFunction",
    runtime=aws_lambda.Runtime.PYTHON_3_13,
    code=aws_lambda.Code.from_asset(
        "../../tools/dist/lambda.zip"  # Pre-built by nx
    )
)
```

**Pros:**

- ✅ **Simplest approach**: One zip file, no layers
- ✅ **Self-contained**: All dependencies included
- ✅ **No layer versioning**: Everything updates together

**Cons:**

- ⚠️ **Every deployment**: Re-uploads all dependencies
- ⚠️ **Size limits**: Still 250MB unzipped
- ⚠️ **Slow deployment**: Larger package = longer upload

**When to Use:**

- Proof of concept
- Very small dependencies
- Infrequent updates

---

## Recommendation: **Option A - Container Image**

**Rationale:**

1. **Your dependencies require it**: `numpy`, `scipy`, `psycopg[binary]` are heavy (~150MB)
2. **Consistent with existing workflow**: Already using Docker for all services
3. **Future-proof**: Easy to add more analysis libraries
4. **Testing**: Can run locally with same image
5. **Build pipeline**: Already have Docker build expertise

**Estimated sizes:**

- Base Python 3.13 Lambda image: ~500MB
- - NumPy, SciPy: ~150MB
- - psycopg: ~20MB
- - Your code: ~5MB
- **Total**: ~675MB (well under 10GB limit)

---

## Part 2: Detailed Implementation Plan

### Phase 1: Extract Shared Logic Library

**Goal**: Create reusable library for leaderboard snapshot generation logic

#### 1.1 Motivation

The current implementation in `apps/bixarena/tools/` contains business logic (Bradley-Terry ranking, snapshot generation) mixed with CLI tooling. Following monorepo best practices:

**Current state (anti-pattern):**

```
apps/bixarena/tools/          # App containing business logic ❌
└── bixarena_tools/
    └── leaderboard/
        ├── db_helper.py      # Database queries (business logic)
        ├── rank_battle.py    # Bradley-Terry algorithm (business logic)
        └── leaderboard.py    # CLI + business logic mixed
```

**Problems:**

- ❌ Apps cannot depend on other apps (violates monorepo rules)
- ❌ Business logic trapped in CLI tool, not reusable
- ❌ Lambda and AI service cannot share code
- ❌ Testing requires running CLI interface

**Target state (following conventions):**

```
libs/bixarena/leaderboard/    # Library with business logic ✅
└── python/
    └── bixarena_leaderboard/
        ├── snapshot.py       # Snapshot generation (business logic)
        ├── ranking.py        # Bradley-Terry algorithm (business logic)
        └── database.py       # Database queries (business logic)

apps/bixarena/tools/          # App consuming library ✅
└── bixarena_tools/
    └── leaderboard/
        └── leaderboard.py    # CLI interface only (depends on lib)
```

**Benefits:**

- ✅ Libraries can be consumed by both apps and other libs
- ✅ Business logic is reusable, testable, and versioned independently
- ✅ Apps remain thin (UI/CLI/API only)
- ✅ Clear separation of concerns

#### 1.2 Create Domain-Specific Library Package

**Location**: `libs/bixarena/leaderboard/python/`

**Naming conventions:**

- **Nx project name**: `bixarena-leaderboard-python` (matches folder path per naming rule)
- **Python/uv project name**: `bixarena-leaderboard` (no "python" suffix in Python ecosystem)
- **Package name**: `bixarena_leaderboard` (Python import name)

**Structure:**

```
libs/bixarena/leaderboard/python/
├── pyproject.toml                    # Python package definition
├── project.json                      # Nx project configuration
├── README.md
├── bixarena_leaderboard/             # Package (importable code)
│   ├── __init__.py
│   ├── snapshot.py                   # SnapshotGenerator, SnapshotConfig
│   ├── database.py                   # DB queries (from db_helper.py)
│   ├── ranking.py                    # Bradley-Terry (from rank_battle.py)
│   └── config.py                     # Configuration dataclasses
└── tests/
    ├── __init__.py
    ├── test_snapshot.py
    ├── test_ranking.py
    └── test_database.py
```

**Core API:**

```python
# bixarena_leaderboard/snapshot.py
from dataclasses import dataclass
from typing import Optional

@dataclass
class SnapshotConfig:
    """Configuration for snapshot generation."""
    database_url: str
    leaderboard_id: str = "overall"
    num_bootstrap: int = 100
    min_evaluations: int = 0
    rank_by_significance: bool = False

class SnapshotGenerator:
    """Generate leaderboard snapshots from battle evaluations."""

    def __init__(self, config: SnapshotConfig):
        self.config = config

    def generate_snapshot(self, auto_publish: bool = True) -> dict:
        """Generate a new snapshot and save to database.

        Args:
            auto_publish: If True, set snapshot visibility to public immediately

        Returns:
            Dict with snapshot_id, entry_count, evaluation_count
        """
        # Extract from existing leaderboard.py:snapshot_add()
        # 1. Fetch data from DB
        # 2. Compute Bradley-Terry rankings
        # 3. Insert snapshot and entries
        # 4. Set visibility=True if auto_publish=True
        # 5. Return metadata
        pass
```

**pyproject.toml:**

```toml
[project]
name = "bixarena-leaderboard"  # No "python" suffix in Python ecosystem
version = "1.0.0"
description = "BixArena leaderboard snapshot generation and ranking algorithms"
requires-python = "==3.13.3"
dependencies = [
    "numpy>=1.26.0",
    "psycopg[binary]>=3.0.0",
    "scipy>=1.11.0",
]

[tool.hatch.build.targets.wheel]
packages = ["bixarena_leaderboard"]

[build-system]
requires = ["hatchling"]
build-backend = "hatchling.build"
```

**project.json (Nx configuration):**

```json
{
  "name": "bixarena-leaderboard-python",
  "$schema": "../../../../node_modules/nx/schemas/project-schema.json",
  "sourceRoot": "libs/bixarena/leaderboard/python/bixarena_leaderboard",
  "projectType": "library",
  "targets": {
    "build": {
      "executor": "@nxlv/python:build",
      "outputs": ["{projectRoot}/dist"],
      "options": {
        "outputPath": "{projectRoot}/dist",
        "publish": false,
        "lockedVersions": true,
        "bundleLocalDependencies": false
      }
    },
    "test": {
      "executor": "@nxlv/python:run-commands",
      "outputs": [],
      "options": {
        "command": "uv run pytest tests/",
        "cwd": "{projectRoot}"
      }
    }
  },
  "tags": ["type:library", "scope:backend", "language:python", "domain:leaderboard"]
}
```

#### 1.3 Migrate Existing Code

**Move from** `apps/bixarena/tools/bixarena_tools/leaderboard/`:

- ✅ `db_helper.py` → `libs/bixarena/leaderboard/python/bixarena_leaderboard/database.py`
- ✅ `rank_battle.py` → `libs/bixarena/leaderboard/python/bixarena_leaderboard/ranking.py`
- ✅ Core logic from `leaderboard.py:snapshot_add()` → `bixarena_leaderboard/snapshot.py`

**Keep in tools package** (CLI becomes thin wrapper):

```python
# apps/bixarena/tools/bixarena_tools/leaderboard/leaderboard.py
from bixarena_leaderboard import SnapshotGenerator, SnapshotConfig  # Import from lib

@snapshot_app.command("add")
def snapshot_add(...):
    """Create a new leaderboard snapshot."""
    with get_db_connection() as conn:
        # Database connection still managed by CLI tool
        database_url = build_database_url(conn)

        # Use library for business logic
        config = SnapshotConfig(
            database_url=database_url,
            leaderboard_id=id,
            num_bootstrap=num_bootstrap,
            min_evaluations=min_evals,
            rank_by_significance=significant,
        )

        generator = SnapshotGenerator(config)
        result = generator.generate_snapshot(auto_publish=True)

        # CLI-specific display logic
        display_leaderboard_summary(result)
```

**Update tools pyproject.toml:**

```toml
dependencies = [
    # ... existing deps ...
    "bixarena-leaderboard",  # New dependency on lib
]

[tool.uv.sources]
bixarena-leaderboard = { workspace = true }
```

---

### Phase 2: Lambda Function Implementation

#### 2.1 Lambda Handler Code

```
apps/bixarena/functions/leaderboard-snapshot/
├── Dockerfile
├── lambda_function.py
├── requirements.txt
└── README.md
```

**lambda_function.py:**

```python
"""Lambda handler for scheduled leaderboard snapshot generation."""
import boto3
import json
import logging
import os
import time
import uuid
from datetime import UTC, datetime
from typing import Any

from bixarena_leaderboard import SnapshotConfig, SnapshotGenerator

logger = logging.getLogger()
logger.setLevel(logging.INFO)

sns_client = boto3.client("sns")


def validate_inputs(event: dict) -> None:
    """Validate and sanitize Lambda event inputs."""
    # Whitelist allowed leaderboards
    allowed_leaderboards = ["overall", "open-source", "multimodal"]
    leaderboard_id = event.get("leaderboard_id", "overall")
    if leaderboard_id not in allowed_leaderboards:
        raise ValueError(f"Invalid leaderboard_id: {leaderboard_id}")

    # Validate bootstrap iterations (reasonable range)
    num_bootstrap = event.get("num_bootstrap", 100)
    if not isinstance(num_bootstrap, int) or num_bootstrap < 10 or num_bootstrap > 10000:
        raise ValueError(f"Invalid num_bootstrap: {num_bootstrap}")

    # Validate trigger source
    trigger_source = event.get("trigger_source", "scheduled")
    if trigger_source not in ["scheduled", "api"]:
        raise ValueError(f"Invalid trigger_source: {trigger_source}")


def get_database_url() -> str:
    """Retrieve database credentials from Secrets Manager."""
    secret_arn = os.environ["DB_SECRET_ARN"]

    client = boto3.client("secretsmanager")
    response = client.get_secret_value(SecretId=secret_arn)
    secret = json.loads(response["SecretString"])

    return (
        f"postgresql://{secret['username']}:{secret['password']}"
        f"@{secret['host']}:{secret['port']}/{secret['dbname']}"
    )


def handler(event: dict[str, Any], context: Any) -> dict[str, Any]:
    """Lambda entry point for snapshot generation.

    Args:
        event: Event payload with metadata
            - trigger_source: "scheduled" or "api"
            - triggered_by: User email (for API) or "system" (for scheduled)
            - correlation_id: Unique trace ID
            - leaderboard_id: Leaderboard to generate
            - num_bootstrap: Bootstrap iterations
        context: Lambda context object

    Returns:
        Response with snapshot metadata
    """
    start_time = time.time()

    # Extract metadata
    trigger_source = event.get("trigger_source", "scheduled")
    triggered_by = event.get("triggered_by", "system")
    correlation_id = event.get("correlation_id", str(uuid.uuid4()))
    leaderboard_id = event.get("leaderboard_id", "overall")
    num_bootstrap = event.get("num_bootstrap", 100)

    # Validate inputs
    try:
        validate_inputs(event)
    except ValueError as e:
        logger.error(f"Input validation failed: {str(e)}")
        return {
            "statusCode": 400,
            "body": json.dumps({"error": str(e)}),
        }

    logger.info(
        "Snapshot generation started",
        extra={
            "trigger_source": trigger_source,
            "triggered_by": triggered_by,
            "correlation_id": correlation_id,
            "leaderboard_id": leaderboard_id,
        }
    )

    # Get database URL from Secrets Manager
    database_url = get_database_url()

    # Configuration
    config = SnapshotConfig(
        database_url=database_url,
        leaderboard_id=leaderboard_id,
        num_bootstrap=num_bootstrap,
        min_evaluations=int(os.getenv("MIN_EVALUATIONS", "0")),
        rank_by_significance=os.getenv("RANK_BY_SIGNIFICANCE", "false").lower() == "true",
    )

    try:
        generator = SnapshotGenerator(config)
        result = generator.generate_snapshot(auto_publish=True)

        duration_seconds = int(time.time() - start_time)

        # Publish success notification to SNS
        topic_arn = os.environ.get("NOTIFICATION_TOPIC_ARN")
        if topic_arn:
            sns_client.publish(
                TopicArn=topic_arn,
                Subject="Leaderboard Snapshot Generated",
                Message=json.dumps({
                    "status": "success",
                    "trigger_source": trigger_source,
                    "triggered_by": triggered_by,
                    "correlation_id": correlation_id,
                    "leaderboard_id": leaderboard_id,
                    "snapshot_id": result["snapshot_id"],
                    "entry_count": result["entry_count"],
                    "evaluation_count": result["evaluation_count"],
                    "duration_seconds": duration_seconds,
                    "timestamp": datetime.now(UTC).isoformat(),
                }),
            )

        logger.info(
            f"Snapshot created successfully: {result}",
            extra={
                "correlation_id": correlation_id,
                "snapshot_id": result["snapshot_id"],
            }
        )

        return {
            "statusCode": 200,
            "body": json.dumps({
                "message": "Snapshot generated successfully",
                "correlation_id": correlation_id,
                "snapshot_id": result["snapshot_id"],
                "entry_count": result["entry_count"],
                "evaluation_count": result["evaluation_count"],
                "duration_seconds": duration_seconds,
            }),
        }

    except Exception as e:
        duration_seconds = int(time.time() - start_time)

        # Publish failure notification to SNS
        topic_arn = os.environ.get("NOTIFICATION_TOPIC_ARN")
        if topic_arn:
            sns_client.publish(
                TopicArn=topic_arn,
                Subject="Leaderboard Snapshot Failed",
                Message=json.dumps({
                    "status": "failure",
                    "trigger_source": trigger_source,
                    "triggered_by": triggered_by,
                    "correlation_id": correlation_id,
                    "leaderboard_id": leaderboard_id,
                    "error": str(e),
                    "duration_seconds": duration_seconds,
                    "timestamp": datetime.now(UTC).isoformat(),
                }),
            )

        logger.error(
            f"Failed to generate snapshot: {str(e)}",
            extra={
                "correlation_id": correlation_id,
                "trigger_source": trigger_source,
            },
            exc_info=True
        )

        return {
            "statusCode": 500,
            "body": json.dumps({
                "message": "Snapshot generation failed",
                "correlation_id": correlation_id,
                "error": str(e),
            }),
        }
```

**Dockerfile:**

```dockerfile
FROM public.ecr.aws/lambda/python:3.13

# Install system dependencies (if needed for psycopg)
RUN microdnf install -y \
    gcc \
    postgresql-devel \
    && microdnf clean all

# Copy leaderboard library from workspace
COPY ../../../libs/bixarena/leaderboard/python /tmp/bixarena-leaderboard
RUN pip install /tmp/bixarena-leaderboard && rm -rf /tmp/bixarena-leaderboard

# Copy Lambda function
COPY lambda_function.py ${LAMBDA_TASK_ROOT}/
COPY requirements.txt ${LAMBDA_TASK_ROOT}/

# Install Lambda-specific dependencies
RUN pip install -r ${LAMBDA_TASK_ROOT}/requirements.txt --target ${LAMBDA_TASK_ROOT}

CMD ["lambda_function.handler"]
```

**requirements.txt:**

```
# Leaderboard library will be installed separately (from workspace)
# Add any Lambda-specific dependencies here
boto3>=1.40.0  # AWS SDK (included in Lambda runtime, but pin version)
```

#### 2.2 CDK Stack Implementation

**File**: `apps/bixarena/infra/cdk/bixarena_infra_cdk/shared/stacks/leaderboard_snapshot_stack.py`

```python
"""CDK stack for leaderboard snapshot automation."""
from aws_cdk import (
    Duration,
    Stack,
    aws_ec2 as ec2,
    aws_ecr_assets as ecr_assets,
    aws_events as events,
    aws_events_targets as targets,
    aws_iam as iam,
    aws_lambda as lambda_,
    aws_logs as logs,
    aws_rds as rds,
)
from constructs import Construct


class LeaderboardSnapshotStack(Stack):
    """Stack for automated leaderboard snapshot generation."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        vpc: ec2.IVpc,
        database_instance: rds.IDatabaseInstance,
        database_secret_arn: str,
        environment_name: str,
        **kwargs,
    ) -> None:
        super().__init__(scope, construct_id, **kwargs)

        # Build and push Docker image to ECR
        lambda_image = ecr_assets.DockerImageAsset(
            self,
            "LeaderboardSnapshotImage",
            directory="../../functions/leaderboard-snapshot",
            platform=ecr_assets.Platform.LINUX_AMD64,
        )

        # Lambda function
        snapshot_function = lambda_.DockerImageFunction(
            self,
            "LeaderboardSnapshotFunction",
            code=lambda_.DockerImageCode.from_ecr(
                repository=lambda_image.repository,
                tag=lambda_image.asset_hash,
            ),
            timeout=Duration.minutes(15),  # Max Lambda timeout
            memory_size=2048,  # 2GB for NumPy/SciPy
            vpc=vpc,
            vpc_subnets=ec2.SubnetSelection(
                subnet_type=ec2.SubnetType.PRIVATE_WITH_EGRESS
            ),
            environment={
                "DATABASE_URL": f"postgresql://{{username}}:{{password}}@{database_instance.db_instance_endpoint_address}:{database_instance.db_instance_endpoint_port}/bixarena",
                "LEADERBOARD_ID": "overall",
                "NUM_BOOTSTRAP": "1000",  # Production uses more iterations
                "MIN_EVALUATIONS": "0",
                "RANK_BY_SIGNIFICANCE": "false",
                "LOG_LEVEL": "INFO",
            },
            log_retention=logs.RetentionDays.ONE_MONTH,
            description=f"Generate daily leaderboard snapshots for {environment_name}",
        )

        # Grant database access
        database_instance.grant_connect(snapshot_function)

        # Grant access to read database credentials from Secrets Manager
        snapshot_function.add_to_role_policy(
            iam.PolicyStatement(
                actions=["secretsmanager:GetSecretValue"],
                resources=[database_secret_arn],
            )
        )

        # EventBridge rule - daily at 2 AM UTC
        schedule_rule = events.Rule(
            self,
            "LeaderboardSnapshotSchedule",
            schedule=events.Schedule.cron(
                minute="0",
                hour="2",
                month="*",
                week_day="*",
                year="*",
            ),
            description="Trigger daily leaderboard snapshot generation",
        )

        # Add Lambda as target
        schedule_rule.add_target(
            targets.LambdaFunction(
                snapshot_function,
                retry_attempts=2,
            )
        )

        # CloudWatch Dashboard (optional)
        # TODO: Add metrics for snapshot generation success/failure

        # Outputs
        self.snapshot_function = snapshot_function
        self.schedule_rule = schedule_rule
```

**Integration in `dev/app.py`:**

```python
from bixarena_infra_cdk.shared.stacks.leaderboard_snapshot_stack import (
    LeaderboardSnapshotStack,
)

# After database_stack is created...
snapshot_stack = LeaderboardSnapshotStack(
    app,
    f"BixarenaSnapshotStack-{env_name}",
    vpc=vpc_stack.vpc,
    database_instance=database_stack.database_instance,
    database_secret_arn=database_stack.database_secret.secret_arn,
    environment_name=env_name,
    env=env,
)
snapshot_stack.add_dependency(database_stack)
snapshot_stack.add_dependency(vpc_stack)
```

---

### Phase 3: AI Service Endpoint Implementation

#### 3.1 Add Admin Endpoint

**File**: `apps/bixarena/ai-service/bixarena_ai_service/jobs/__init__.py`

```python
"""Background jobs and admin operations."""
```

**File**: `apps/bixarena/ai-service/bixarena_ai_service/routers/admin.py`

```python
"""Admin operations router."""
import boto3
import json
import logging
import os
import uuid
from typing import Annotated

from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel, Field

from bixarena_ai_service.security.jwt_validator import validate_jwt

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/admin", tags=["Admin Operations"])
lambda_client = boto3.client("lambda")


class SnapshotRequest(BaseModel):
    """Request to generate a leaderboard snapshot."""

    leaderboard_id: str = Field(
        default="overall", description="Leaderboard ID to generate snapshot for"
    )
    num_bootstrap: int = Field(
        default=100, ge=10, le=10000, description="Bootstrap iterations"
    )


class SnapshotResponse(BaseModel):
    """Response for async snapshot generation."""

    message: str
    correlation_id: str
    leaderboard_id: str
    status: str = "initiated"


async def validate_jwt_admin(
    jwt_claims: Annotated[dict, Depends(validate_jwt)],
) -> str:
    """Validate JWT and ensure user has admin role.

    Returns:
        User email for audit logging

    Raises:
        HTTPException: If user does not have admin role
    """
    roles = jwt_claims.get("cognito:groups", [])
    email = jwt_claims.get("email", jwt_claims.get("sub"))

    if "admin" not in roles:
        logger.warning(
            f"User {email} attempted snapshot generation without admin role",
            extra={"user_email": email, "roles": roles}
        )
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Admin role required for snapshot generation",
        )

    return email


@router.post(
    "/generate-snapshot",
    response_model=SnapshotResponse,
    status_code=status.HTTP_202_ACCEPTED,
    summary="Trigger leaderboard snapshot generation",
    description="""
    Asynchronously trigger leaderboard snapshot generation.

    This endpoint invokes a Lambda function and returns immediately with a correlation ID.
    The snapshot generation runs in the background (takes 2-5 minutes).
    You will receive a Slack notification when complete.

    Requires authentication with admin role.
    """,
)
async def generate_snapshot(
    request: SnapshotRequest,
    user_email: Annotated[str, Depends(validate_jwt_admin)],
) -> SnapshotResponse:
    """Trigger asynchronous leaderboard snapshot generation.

    Args:
        request: Snapshot generation parameters
        user_email: Email of authenticated admin user

    Returns:
        Response with correlation ID for tracking
    """
    correlation_id = str(uuid.uuid4())
    lambda_function_name = os.getenv("SNAPSHOT_LAMBDA_NAME")

    if not lambda_function_name:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Snapshot Lambda function not configured",
        )

    logger.info(
        f"Snapshot generation triggered by {user_email}",
        extra={
            "user_email": user_email,
            "correlation_id": correlation_id,
            "leaderboard_id": request.leaderboard_id,
        }
    )

    try:
        # Invoke Lambda asynchronously
        lambda_client.invoke(
            FunctionName=lambda_function_name,
            InvocationType="Event",  # Async invocation (fire-and-forget)
            Payload=json.dumps({
                "trigger_source": "api",
                "triggered_by": user_email,
                "correlation_id": correlation_id,
                "leaderboard_id": request.leaderboard_id,
                "num_bootstrap": request.num_bootstrap,
            }),
        )

        logger.info(
            f"Lambda invoked successfully for correlation_id {correlation_id}",
            extra={"correlation_id": correlation_id}
        )

        return SnapshotResponse(
            message=(
                "Snapshot generation initiated. "
                "You will receive a Slack notification when complete."
            ),
            correlation_id=correlation_id,
            leaderboard_id=request.leaderboard_id,
            status="initiated",
        )

    except lambda_client.exceptions.ServiceException as e:
        logger.error(
            f"Failed to invoke Lambda: {str(e)}",
            extra={"correlation_id": correlation_id},
            exc_info=True
        )
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to trigger snapshot generation: {str(e)}",
        )
```

**Register router in `app.py`:**

```python
from bixarena_ai_service.routers.admin import router as admin_router

# After middleware setup
app.include_router(admin_router)
```

#### 3.2 Update AI Service Dependencies

**Add boto3 to `pyproject.toml`:**

```toml
dependencies = [
    # ... existing deps ...
    "boto3>=1.40.0",  # AWS SDK for Lambda invocation
]
```

**Add environment variable to AI service:**

```python
# In CDK stack for AI service ECS task definition
task_definition.add_container(
    "AIServiceContainer",
    environment={
        # ... existing env vars ...
        "SNAPSHOT_LAMBDA_NAME": snapshot_lambda.function_name,
    },
)

# Grant ECS task role permission to invoke Lambda
snapshot_lambda.grant_invoke(ai_service_task_role)
```

---

### Phase 4: Database Connection Strategy

#### Option A: RDS Proxy (Recommended for Lambda)

**Why RDS Proxy:**

- Connection pooling for Lambda (many concurrent invocations)
- Automatic failover
- IAM authentication (no password in env vars)
- Reduced DB connections

**CDK Implementation:**

```python
# In leaderboard_snapshot_stack.py
from aws_cdk import aws_rds as rds

# Create RDS Proxy
db_proxy = rds.DatabaseProxy(
    self,
    "LeaderboardDBProxy",
    proxy_target=rds.ProxyTarget.from_instance(database_instance),
    vpc=vpc,
    secrets=[database_secret],
    require_tls=True,
)

# Update Lambda environment
snapshot_function = lambda_.DockerImageFunction(
    # ...
    environment={
        "DATABASE_URL": f"postgresql://{{username}}:{{password}}@{db_proxy.endpoint}/bixarena",
        # ...
    },
)

# Grant proxy access
db_proxy.grant_connect(snapshot_function, "postgres")
```

#### Option B: Direct Connection (Simpler for AI Service)

**AI Service** can use direct connection (already configured):

- Long-lived service maintains connection pool
- No cold start issues
- Simpler configuration

---

### Phase 5: Testing Strategy

#### 5.1 Local Testing

**Test Lambda Locally:**

```bash
# Build Docker image
cd apps/bixarena/functions/leaderboard-snapshot
docker build -t leaderboard-snapshot-local .

# Run with local PostgreSQL (port forwarded)
docker run -p 9000:8080 \
  -e DATABASE_URL="postgresql://postgres:changeme@host.docker.internal:5432/bixarena" \
  -e LEADERBOARD_ID="overall" \
  -e NUM_BOOTSTRAP="10" \
  leaderboard-snapshot-local

# Invoke
curl -X POST "http://localhost:9000/2015-03-31/functions/function/invocations" \
  -d '{}'
```

**Test AI Service Endpoint:**

```bash
# Get session cookie from browser
SESSION_ID="your-session-id"

# Call admin endpoint
curl -X POST "http://localhost:8113/admin/generate-snapshot" \
  -H "Cookie: JSESSIONID=${SESSION_ID}" \
  -H "Content-Type: application/json" \
  -d '{
    "leaderboard_id": "overall",
    "num_bootstrap": 10
  }'
```

#### 5.2 Integration Testing

**Test in AWS Dev Environment:**

```bash
# Deploy stack
nx run bixarena-infra-cdk:deploy:dev --stacks="*Snapshot*"

# Manually invoke Lambda
aws lambda invoke \
  --function-name bixarena-dev-{name}-leaderboard-snapshot \
  --profile bixarena-dev-Developer \
  response.json

# Check logs
aws logs tail /aws/lambda/bixarena-dev-{name}-leaderboard-snapshot \
  --follow \
  --profile bixarena-dev-Developer
```

---

## Phase 6: Deployment Workflow

### 6.1 Build Pipeline

**Update `project.json` in `apps/bixarena/functions/leaderboard-snapshot`:**

```json
{
  "name": "leaderboard-snapshot-function",
  "projectType": "application",
  "targets": {
    "build-image": {
      "executor": "nx:run-commands",
      "options": {
        "command": "docker build -t leaderboard-snapshot:latest .",
        "cwd": "{projectRoot}"
      },
      "dependsOn": ["^build"]
    },
    "test-local": {
      "executor": "nx:run-commands",
      "options": {
        "command": "docker run --rm -p 9000:8080 leaderboard-snapshot:latest",
        "cwd": "{projectRoot}"
      },
      "dependsOn": ["build-image"]
    }
  },
  "tags": ["type:function", "scope:backend", "language:python"]
}
```

### 6.2 CI/CD Integration

**GitHub Actions** (example):

```yaml
# .github/workflows/deploy-bixarena-infra.yml
- name: Deploy Snapshot Function
  run: |
    nx run bixarena-infra-cdk:deploy:dev \
      --stacks="BixarenaSnapshotStack-dev" \
      --require-approval=never
```

---

## Phase 7: Monitoring & Observability

### 7.1 CloudWatch Metrics

**Lambda Metrics (automatic):**

- Invocations
- Duration
- Errors
- Throttles
- Concurrent executions

**Custom Metrics:**

```python
import boto3

cloudwatch = boto3.client("cloudwatch")

# After snapshot generation
cloudwatch.put_metric_data(
    Namespace="BixArena/Leaderboard",
    MetricData=[
        {
            "MetricName": "SnapshotEntryCount",
            "Value": result["entry_count"],
            "Unit": "Count",
        },
        {
            "MetricName": "SnapshotGenerationDuration",
            "Value": duration_seconds,
            "Unit": "Seconds",
        },
    ],
)
```

### 7.2 Alarms

**CDK Implementation:**

```python
from aws_cdk import aws_cloudwatch as cloudwatch, aws_sns as sns

# Create SNS topic for alerts
alert_topic = sns.Topic(self, "SnapshotAlerts")

# Alarm on function errors
error_alarm = cloudwatch.Alarm(
    self,
    "SnapshotErrorAlarm",
    metric=snapshot_function.metric_errors(),
    threshold=1,
    evaluation_periods=1,
    alarm_description="Leaderboard snapshot generation failed",
)

error_alarm.add_alarm_action(cloudwatch_actions.SnsAction(alert_topic))
```

### 7.3 Slack Notifications

**Architecture:**

```
Snapshot Lambda (success) → SNS Topic → Slack Notifier Lambda → Slack Webhook API
```

**Slack Message Format:**

```json
{
  "text": "🎯 New Leaderboard Snapshot Generated!",
  "blocks": [
    {
      "type": "section",
      "text": {
        "type": "mrkdwn",
        "text": "*Leaderboard Snapshot Generated* ✅\n\n*Leaderboard:* Overall\n*Snapshot ID:* snapshot_2026-01-09_02-00\n*Models Ranked:* 42\n*Total Evaluations:* 1,247\n*Bootstrap Iterations:* 1000\n*Duration:* 87 seconds"
      }
    },
    {
      "type": "actions",
      "elements": [
        {
          "type": "button",
          "text": {
            "type": "plain_text",
            "text": "View Leaderboard"
          },
          "url": "https://bixarena.io/leaderboard"
        }
      ]
    }
  ]
}
```

**CDK Implementation:**

Add to `leaderboard_snapshot_stack.py`:

```python
from aws_cdk import (
    aws_lambda as lambda_,
    aws_sns as sns,
    aws_sns_subscriptions as subscriptions,
    aws_secretsmanager as secretsmanager,
)

# SNS Topic for snapshot events
snapshot_events_topic = sns.Topic(
    self,
    "SnapshotEventsTopic",
    display_name="Leaderboard Snapshot Events",
)

# Slack webhook URL stored in Secrets Manager
slack_webhook_secret = secretsmanager.Secret.from_secret_name_v2(
    self,
    "SlackWebhookSecret",
    secret_name="bixarena/slack/webhook-url",
)

# Slack notification Lambda (inline for simplicity)
slack_notifier = lambda_.Function(
    self,
    "SlackNotifier",
    runtime=lambda_.Runtime.PYTHON_3_13,
    handler="index.handler",
    code=lambda_.Code.from_inline("""
import json
import os
import urllib.request
import urllib.error

def handler(event, context):
    webhook_url = os.environ['SLACK_WEBHOOK_URL']

    # Parse SNS message
    for record in event['Records']:
        message = json.loads(record['Sns']['Message'])

        status = message.get('status', 'unknown')
        trigger_source = message.get('trigger_source', 'scheduled')
        triggered_by = message.get('triggered_by', 'System')

        # Build title and trigger info based on source
        if trigger_source == 'api':
            title = f"🎯 Leaderboard Snapshot {'Generated' if status == 'success' else 'Failed'} (Manual)"
            trigger_info = f"*Triggered by:* {triggered_by}"
        else:
            title = f"🎯 Leaderboard Snapshot {'Generated' if status == 'success' else 'Failed'} (Scheduled)"
            trigger_info = f"*Triggered by:* Automated daily job"

        # Build status emoji and message
        if status == 'success':
            status_emoji = "✅"
            status_text = "Snapshot Generated"
        else:
            status_emoji = "❌"
            status_text = "Snapshot Failed"

        # Build Slack message
        slack_message = {
            'text': title,
            'blocks': [
                {
                    'type': 'section',
                    'text': {
                        'type': 'mrkdwn',
                        'text': f"*{status_text}* {status_emoji}\\n\\n"
                                f"{trigger_info}\\n"
                                f"*Leaderboard:* {message.get('leaderboard_id', 'N/A')}\\n"
                                + (f"*Snapshot ID:* {message.get('snapshot_id', 'N/A')}\\n"
                                   f"*Models Ranked:* {message.get('entry_count', 'N/A')}\\n"
                                   f"*Total Evaluations:* {message.get('evaluation_count', 'N/A')}\\n"
                                   if status == 'success' else
                                   f"*Error:* {message.get('error', 'Unknown error')}\\n") +
                                f"*Duration:* {message.get('duration_seconds', 'N/A')}s"
                    }
                },
                {
                    'type': 'context',
                    'elements': [
                        {
                            'type': 'mrkdwn',
                            'text': f"Correlation ID: `{message.get('correlation_id', 'N/A')}`"
                        }
                    ]
                }
            ]
        }

        # Add action button only for success
        if status == 'success':
            slack_message['blocks'].append({
                'type': 'actions',
                'elements': [
                    {
                        'type': 'button',
                        'text': {'type': 'plain_text', 'text': 'View Leaderboard'},
                        'url': 'https://bixarena.io/leaderboard'
                    }
                ]
            })

        # Send to Slack
        req = urllib.request.Request(
            webhook_url,
            data=json.dumps(slack_message).encode('utf-8'),
            headers={'Content-Type': 'application/json'}
        )

        try:
            urllib.request.urlopen(req)
            print(f"Sent notification for snapshot: {message.get('snapshot_id')}")
        except urllib.error.HTTPError as e:
            print(f"Failed to send Slack notification: {e}")
            raise

    return {'statusCode': 200}
"""),
    environment={
        "SLACK_WEBHOOK_URL": slack_webhook_secret.secret_value.unsafe_unwrap(),
    },
    timeout=Duration.seconds(30),
    description="Send Slack notifications for leaderboard snapshots",
)

# Grant access to read Slack webhook secret
slack_webhook_secret.grant_read(slack_notifier)

# Subscribe Slack notifier to SNS topic
snapshot_events_topic.add_subscription(
    subscriptions.LambdaSubscription(slack_notifier)
)

# Update main snapshot Lambda to publish to SNS
snapshot_function.add_environment(
    "NOTIFICATION_TOPIC_ARN", snapshot_events_topic.topic_arn
)
snapshot_events_topic.grant_publish(snapshot_function)
```

**Update Lambda handler to publish SNS:**

In `lambda_function.py`:

```python
import boto3
import time
from datetime import UTC, datetime

sns_client = boto3.client("sns")

def handler(event: dict[str, Any], context: Any) -> dict[str, Any]:
    start_time = time.time()

    # ... existing snapshot generation code ...

    try:
        generator = SnapshotGenerator(config)
        result = generator.generate_snapshot()

        duration_seconds = int(time.time() - start_time)

        # Publish success notification to SNS
        topic_arn = os.environ.get("NOTIFICATION_TOPIC_ARN")
        if topic_arn:
            sns_client.publish(
                TopicArn=topic_arn,
                Subject="Leaderboard Snapshot Generated",
                Message=json.dumps({
                    "status": "success",
                    "leaderboard_id": config.leaderboard_id,
                    "snapshot_id": result["snapshot_id"],
                    "entry_count": result["entry_count"],
                    "evaluation_count": result["evaluation_count"],
                    "duration_seconds": duration_seconds,
                    "timestamp": datetime.now(UTC).isoformat(),
                }),
            )

        logger.info(f"Snapshot created successfully: {result}")

        return {
            "statusCode": 200,
            "body": json.dumps({
                "message": "Snapshot generated successfully",
                "snapshot_id": result["snapshot_id"],
                "entry_count": result["entry_count"],
                "evaluation_count": result["evaluation_count"],
                "duration_seconds": duration_seconds,
            }),
        }

    except Exception as e:
        # Publish failure notification
        duration_seconds = int(time.time() - start_time)

        topic_arn = os.environ.get("NOTIFICATION_TOPIC_ARN")
        if topic_arn:
            sns_client.publish(
                TopicArn=topic_arn,
                Subject="Leaderboard Snapshot Failed",
                Message=json.dumps({
                    "status": "failure",
                    "leaderboard_id": config.leaderboard_id,
                    "error": str(e),
                    "duration_seconds": duration_seconds,
                    "timestamp": datetime.now(UTC).isoformat(),
                }),
            )

        logger.error(f"Failed to generate snapshot: {str(e)}", exc_info=True)
        raise
```

**Slack Webhook Setup:**

1. Create Slack App at https://api.slack.com/apps
2. Enable Incoming Webhooks
3. Add webhook to desired channel (e.g., `#bixarena-alerts`)
4. Copy webhook URL
5. Store in AWS Secrets Manager:

```bash
aws secretsmanager create-secret \
  --name bixarena/slack/webhook-url \
  --secret-string "https://hooks.slack.com/services/YOUR/WEBHOOK/URL" \
  --profile bixarena-dev-Developer
```

---

## Implementation Timeline

### Week 1: Foundation

- [ ] Create `bixarena-leaderboard` library in `libs/bixarena/leaderboard/python/`
- [ ] Extract business logic from `apps/bixarena/tools/bixarena_tools/leaderboard/`
- [ ] Write unit tests for library (snapshot, ranking, database modules)
- [ ] Update `bixarena-tools` CLI to depend on `bixarena-leaderboard` library

### Week 2: Lambda Function

- [ ] Create Lambda function code and Dockerfile
- [ ] Test Lambda locally with Docker
- [ ] Create CDK stack for Lambda and EventBridge
- [ ] Set up Slack webhook in AWS Secrets Manager
- [ ] Add SNS topic and Slack notifier Lambda
- [ ] Deploy to dev environment

### Week 3: AI Service Integration

- [ ] Add admin endpoint to AI service with async Lambda invocation
- [ ] Implement JWT authentication with admin role validation
- [ ] Add boto3 dependency for Lambda invocation
- [ ] Grant ECS task role permission to invoke Lambda
- [ ] Add SNAPSHOT_LAMBDA_NAME environment variable
- [ ] Test end-to-end authentication and async invocation flow
- [ ] Deploy to dev environment

### Week 4: Testing & Documentation

- [ ] Integration testing in dev environment
- [ ] Test Slack notifications (success and failure scenarios)
- [ ] Performance testing (snapshot generation time)
- [ ] Verify auto-publish functionality
- [ ] Documentation and runbooks
- [ ] Deploy to stage/prod

---

## Cost Estimation

### Lambda Function

- **Execution**: Daily at 2 AM, ~2 minutes duration
- **Memory**: 2GB
- **Cost per month**: ~$0.05 (negligible)

### RDS Proxy (if used)

- **Cost**: ~$15/month per proxy
- **Note**: Only if connection pooling is needed

### CloudWatch Logs

- **Retention**: 30 days
- **Cost**: ~$0.50/month

**Total estimated cost**: <$20/month

---

## Decisions Made

1. **Snapshot visibility**: ✅ Auto-publish to public (set `visible=true` after generation)
2. **Notification**: ✅ Send Slack notification when snapshot completes
3. **Multiple leaderboards**: ✅ Start with "overall" leaderboard, design for future expansion (open-source, multimodal, etc.)
4. **Execution time**: ✅ Confirmed to be < 15 minutes (within Lambda limits)
5. **On-demand execution**: ✅ Async Lambda invocation (returns HTTP 202 immediately, no timeout issues)
6. **Metadata tracking**: ✅ Track trigger_source, triggered_by, correlation_id for audit trail
7. **Security**:
   - ✅ IAM least privilege for Lambda and ECS task roles
   - ✅ Database credentials in AWS Secrets Manager
   - ✅ Lambda in VPC private subnet with security groups
   - ✅ Input validation (whitelist leaderboard IDs, validate ranges)
   - ✅ JWT authentication with admin role required
   - ✅ Rate limiting on API Gateway (100 requests/day)
8. **Retention**: TBD - Will address in future iteration

---

## Next Steps

### Ready to Begin Implementation ✅

All decisions have been made:

- ✅ Lambda packaging: **Container Image (Docker)**
- ✅ Auto-publish snapshots: **Yes** (set `visible=true`)
- ✅ Notifications: **Slack** via SNS → Lambda → Webhook
- ✅ Initial scope: **"overall" leaderboard** (extensible design)
- ✅ Execution time: **< 15 minutes** (within Lambda limits)
- ✅ On-demand execution: **Async Lambda invocation** (HTTP 202, no timeout)
- ✅ Security: **Defense-in-depth** (IAM, Secrets Manager, VPC, input validation)
- ✅ Audit logging: **Metadata tracking** (trigger_source, triggered_by, correlation_id)

### Recommended Implementation Order

1. **Phase 1 (Week 1)**: Extract leaderboard library

   - Create `libs/bixarena/leaderboard/python/` (Nx: `bixarena-leaderboard-python`, Python: `bixarena-leaderboard`)
   - Move business logic from `apps/bixarena/tools/bixarena_tools/leaderboard/`
   - Add `auto_publish` parameter to `SnapshotGenerator.generate_snapshot()`
   - Write unit tests for snapshot, ranking, and database modules

2. **Phase 2 (Week 2)**: Build Lambda function

   - Create Docker-based Lambda
   - Add SNS notification logic
   - Set up CDK stack with Slack integration
   - Test locally with `docker run`

3. **Phase 3 (Week 3)**: Add AI service endpoint

   - Create `/admin/generate-snapshot` endpoint with async Lambda invocation
   - Implement JWT authentication with admin role validation
   - Grant ECS task role permission to invoke Lambda
   - Add SNAPSHOT_LAMBDA_NAME environment variable
   - Deploy to dev environment

4. **Phase 4 (Week 4)**: Test and deploy
   - Integration testing
   - Verify Slack notifications
   - Deploy to stage/prod

### Quick Start Command

```bash
# Create leaderboard library
mkdir -p libs/bixarena/leaderboard/python/bixarena_leaderboard
cd libs/bixarena/leaderboard/python

# Initialize Python package
cat > pyproject.toml << 'EOF'
[project]
name = "bixarena-leaderboard"
version = "1.0.0"
description = "BixArena leaderboard snapshot generation and ranking algorithms"
requires-python = "==3.13.3"
dependencies = [
    "numpy>=1.26.0",
    "psycopg[binary]>=3.0.0",
    "scipy>=1.11.0",
]

[tool.hatch.build.targets.wheel]
packages = ["bixarena_leaderboard"]

[build-system]
requires = ["hatchling"]
build-backend = "hatchling.build"
EOF

# Initialize Nx project
cat > project.json << 'EOF'
{
  "name": "bixarena-leaderboard-python",
  "projectType": "library",
  "targets": {
    "build": {
      "executor": "@nxlv/python:build",
      "outputs": ["{projectRoot}/dist"]
    },
    "test": {
      "executor": "@nxlv/python:run-commands",
      "options": {
        "command": "uv run pytest tests/",
        "cwd": "{projectRoot}"
      }
    }
  },
  "tags": ["type:library", "scope:backend", "language:python", "domain:leaderboard"]
}
EOF

# Create package structure
mkdir -p bixarena_leaderboard tests
touch bixarena_leaderboard/{__init__.py,snapshot.py,database.py,ranking.py,config.py}
touch tests/{__init__.py,test_snapshot.py,test_database.py,test_ranking.py}

# Start implementation!
```
