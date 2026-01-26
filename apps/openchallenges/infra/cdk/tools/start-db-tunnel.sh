#!/bin/bash
# Script to establish port forwarding to RDS via bastion ECS service
# Usage: ./start-db-tunnel.sh [dev|stage|prod] [developer-name]
#
# Examples:
#   ./start-db-tunnel.sh dev              # Auto-detects dev profile with current username
#   ./start-db-tunnel.sh dev john         # Explicit developer name
#   ./start-db-tunnel.sh stage            # Stage environment (no developer name)
#   ./start-db-tunnel.sh prod             # Production environment (no developer name)

set -e

# Configuration
ENVIRONMENT=${1:-dev}
DEVELOPER_NAME=${2}  # Optional, will be auto-detected if not provided
LOCAL_PORT=5432

echo "=== OpenChallenges Database Port Forwarding ==="
echo "Environment: ${ENVIRONMENT}"

# Function to detect AWS profile
detect_aws_profile() {
  local env=$1
  local profiles=(
    "openchallenges-${env}-Developer"
    "openchallenges-${env}-Administrator"
    "openchallenges-prod-Developer"      # Fallback for stage in prod account
    "openchallenges-prod-Administrator"  # Fallback for stage in prod account
  )

  for profile in "${profiles[@]}"; do
    if aws configure list-profiles 2>/dev/null | grep -q "^${profile}$"; then
      echo "$profile"
      return 0
    fi
  done

  echo ""
  return 1
}

# Detect AWS profile
AWS_PROFILE=$(detect_aws_profile "${ENVIRONMENT}")

if [ -z "$AWS_PROFILE" ]; then
  echo "ERROR: Could not find AWS profile for environment '${ENVIRONMENT}'"
  echo ""
  echo "Expected one of:"
  echo "  - openchallenges-${ENVIRONMENT}-Developer"
  echo "  - openchallenges-${ENVIRONMENT}-Administrator"
  if [ "${ENVIRONMENT}" = "stage" ]; then
    echo "  - openchallenges-prod-Developer (stage in prod account)"
    echo "  - openchallenges-prod-Administrator (stage in prod account)"
  fi
  echo ""
  echo "Available profiles:"
  aws configure list-profiles 2>/dev/null | grep openchallenges || echo "  (none found)"
  exit 1
fi

echo "AWS Profile: ${AWS_PROFILE}"
echo ""

# Check if Session Manager plugin is installed
if ! command -v session-manager-plugin &> /dev/null; then
    echo "ERROR: AWS Session Manager plugin is not installed"
    echo ""
    echo "Install it from:"
    echo "  https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-working-with-install-plugin.html"
    echo ""
    echo "macOS: brew install --cask session-manager-plugin"
    echo "Linux: See AWS documentation"
    exit 1
fi

# Function to detect database secret
detect_database_secret() {
  local env=$1
  local dev_name=$2

  # Build list of patterns to try
  local patterns=()

  if [ -n "$dev_name" ]; then
    # Try with developer name first
    patterns+=("openchallenges-${env}-${dev_name}-database")
  fi

  # Try without developer name
  patterns+=("openchallenges-${env}-database")

  # Try with current username as fallback for dev
  if [ "$env" = "dev" ] && [ -z "$dev_name" ]; then
    patterns+=("openchallenges-${env}-$(whoami)-database")
  fi

  for pattern in "${patterns[@]}"; do
    echo "  Trying pattern: ${pattern}" >&2
    local secret_arn=$(AWS_PROFILE=${AWS_PROFILE} aws secretsmanager list-secrets \
      --query "SecretList[?contains(Name, \`${pattern}\`)].ARN" \
      --output text 2>/dev/null)

    if [ -n "$secret_arn" ] && [ "$secret_arn" != "None" ]; then
      echo "$secret_arn"
      return 0
    fi
  done

  echo ""
  return 1
}

# Get database credentials
echo "1. Retrieving database credentials..."
SECRET_ARN=$(detect_database_secret "${ENVIRONMENT}" "${DEVELOPER_NAME}")

if [ -z "$SECRET_ARN" ]; then
  echo "ERROR: Could not find database secret"
  echo ""
  echo "Tried patterns:"
  if [ -n "$DEVELOPER_NAME" ]; then
    echo "  - openchallenges-${ENVIRONMENT}-${DEVELOPER_NAME}-database"
  fi
  echo "  - openchallenges-${ENVIRONMENT}-database"
  if [ "${ENVIRONMENT}" = "dev" ] && [ -z "$DEVELOPER_NAME" ]; then
    echo "  - openchallenges-${ENVIRONMENT}-$(whoami)-database"
  fi
  echo ""
  echo "Available secrets:"
  AWS_PROFILE=${AWS_PROFILE} aws secretsmanager list-secrets \
    --query "SecretList[?contains(Name, 'openchallenges')].Name" \
    --output text | tr '\t' '\n' | sed 's/^/  - /'
  exit 1
fi

echo "Found secret: ${SECRET_ARN}"

SECRET_JSON=$(AWS_PROFILE=${AWS_PROFILE} aws secretsmanager get-secret-value \
  --secret-id "${SECRET_ARN}" \
  --query 'SecretString' \
  --output text)

DB_HOST=$(echo "$SECRET_JSON" | python3 -c "import sys, json; print(json.load(sys.stdin)['host'])")
DB_PORT=$(echo "$SECRET_JSON" | python3 -c "import sys, json; print(json.load(sys.stdin)['port'])")
DB_NAME=$(echo "$SECRET_JSON" | python3 -c "import sys, json; print(json.load(sys.stdin)['dbname'])")
DB_USER=$(echo "$SECRET_JSON" | python3 -c "import sys, json; print(json.load(sys.stdin)['username'])")
DB_PASSWORD=$(echo "$SECRET_JSON" | python3 -c "import sys, json; print(json.load(sys.stdin)['password'])")

echo "Database: ${DB_HOST}:${DB_PORT}/${DB_NAME}"
echo ""

# Function to detect ECS cluster
detect_ecs_cluster() {
  local env=$1
  local dev_name=$2

  # Build list of cluster patterns to try
  local patterns=()

  if [ -n "$dev_name" ]; then
    # Try with developer name first
    patterns+=("openchallenges-${env}-${dev_name}")
  fi

  # Try without developer name
  patterns+=("openchallenges-${env}")

  # Try with current username as fallback for dev
  if [ "$env" = "dev" ] && [ -z "$dev_name" ]; then
    patterns+=("openchallenges-${env}-$(whoami)")
  fi

  for pattern in "${patterns[@]}"; do
    echo "  Trying cluster: ${pattern}" >&2
    if AWS_PROFILE=${AWS_PROFILE} aws ecs describe-clusters \
      --clusters "${pattern}" \
      --query 'clusters[?status==`ACTIVE`].clusterName' \
      --output text 2>/dev/null | grep -q "${pattern}"; then
      echo "$pattern"
      return 0
    fi
  done

  echo ""
  return 1
}

# Find the cluster
echo "2. Finding ECS cluster and bastion service..."
CLUSTER_NAME=$(detect_ecs_cluster "${ENVIRONMENT}" "${DEVELOPER_NAME}")

if [ -z "$CLUSTER_NAME" ]; then
  echo "ERROR: Could not find ECS cluster"
  echo ""
  echo "Tried clusters:"
  if [ -n "$DEVELOPER_NAME" ]; then
    echo "  - openchallenges-${ENVIRONMENT}-${DEVELOPER_NAME}"
  fi
  echo "  - openchallenges-${ENVIRONMENT}"
  if [ "${ENVIRONMENT}" = "dev" ] && [ -z "$DEVELOPER_NAME" ]; then
    echo "  - openchallenges-${ENVIRONMENT}-$(whoami)"
  fi
  echo ""
  echo "Available clusters:"
  AWS_PROFILE=${AWS_PROFILE} aws ecs list-clusters \
    --query 'clusterArns' \
    --output text | tr '\t' '\n' | grep openchallenges | awk -F/ '{print "  - " $NF}'
  exit 1
fi

echo "Found cluster: ${CLUSTER_NAME}"

# Get bastion service tasks
TASK_ARN=$(AWS_PROFILE=${AWS_PROFILE} aws ecs list-tasks \
  --cluster "${CLUSTER_NAME}" \
  --service-name $(AWS_PROFILE=${AWS_PROFILE} aws ecs list-services \
    --cluster "${CLUSTER_NAME}" \
    --query "serviceArns[?contains(@, 'BastionService')]" \
    --output text | head -1 | awk -F/ '{print $NF}') \
  --desired-status RUNNING \
  --query 'taskArns[0]' \
  --output text)

if [ -z "$TASK_ARN" ] || [ "$TASK_ARN" = "None" ]; then
  echo "ERROR: No running bastion task found in cluster ${CLUSTER_NAME}"
  echo "Is the bastion service running?"
  exit 1
fi

TASK_ID=$(echo "$TASK_ARN" | awk -F/ '{print $NF}')
echo "Bastion task: ${TASK_ID}"
echo ""

# Get runtime ID for ECS Exec target
echo "3. Getting task details..."
RUNTIME_ID=$(AWS_PROFILE=${AWS_PROFILE} aws ecs describe-tasks \
  --cluster "${CLUSTER_NAME}" \
  --tasks "${TASK_ARN}" \
  --query 'tasks[0].containers[?name==`bastion`].runtimeId' \
  --output text)

if [ -z "$RUNTIME_ID" ] || [ "$RUNTIME_ID" = "None" ]; then
  echo "ERROR: Could not get container runtime ID for bastion container"
  exit 1
fi

echo "Container runtime ID: ${RUNTIME_ID}"
echo ""

# Start port forwarding
echo "4. Starting port forwarding session..."
echo "Forwarding localhost:${LOCAL_PORT} -> ${DB_HOST}:${DB_PORT}"
echo ""
echo "=== Port Forwarding Active ==="
echo ""
echo "You can now connect to the database using:"
echo "  psql -h localhost -p ${LOCAL_PORT} -U ${DB_USER} -d ${DB_NAME}"
echo ""
echo "Or use this connection string:"
echo "  postgresql://${DB_USER}:${DB_PASSWORD}@localhost:${LOCAL_PORT}/${DB_NAME}"
echo ""
echo "Or set environment variable:"
echo "  export DATABASE_URL='postgresql://${DB_USER}:${DB_PASSWORD}@localhost:${LOCAL_PORT}/${DB_NAME}'"
echo ""
echo "Press Ctrl+C to stop port forwarding"
echo ""

# Start Session Manager port forwarding to remote host (RDS) through bastion
AWS_PROFILE=${AWS_PROFILE} aws ssm start-session \
  --target "ecs:${CLUSTER_NAME}_${TASK_ID}_${RUNTIME_ID}" \
  --document-name AWS-StartPortForwardingSessionToRemoteHost \
  --parameters "{\"host\":[\"${DB_HOST}\"],\"portNumber\":[\"${DB_PORT}\"],\"localPortNumber\":[\"${LOCAL_PORT}\"]}"

echo ""
echo "Port forwarding closed."
