#!/bin/bash
# Script to establish port forwarding to OpenSearch via bastion ECS service
# Usage: ./start-opensearch-tunnel.sh [dev|stage|prod] [developer-name]
#
# Examples:
#   ./start-opensearch-tunnel.sh dev              # Auto-detects dev profile with current username
#   ./start-opensearch-tunnel.sh dev john         # Explicit developer name
#   ./start-opensearch-tunnel.sh stage            # Stage environment (no developer name)
#   ./start-opensearch-tunnel.sh prod             # Production environment (no developer name)

set -e

# Configuration
ENVIRONMENT=${1:-dev}
DEVELOPER_NAME=${2}  # Optional, will be auto-detected if not provided
LOCAL_PORT=9200

echo "=== OpenChallenges OpenSearch Port Forwarding ==="
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

# Function to detect OpenSearch stack
detect_opensearch_stack() {
  local env=$1
  local dev_name=$2

  # Build list of patterns to try
  local patterns=()

  if [ -n "$dev_name" ]; then
    # Try with developer name first
    patterns+=("openchallenges-${env}-${dev_name}-opensearch")
  fi

  # Try without developer name
  patterns+=("openchallenges-${env}-opensearch")

  # Try with current username as fallback for dev
  if [ "$env" = "dev" ] && [ -z "$dev_name" ]; then
    patterns+=("openchallenges-${env}-$(whoami)-opensearch")
  fi

  for pattern in "${patterns[@]}"; do
    echo "  Trying stack: ${pattern}" >&2
    if AWS_PROFILE=${AWS_PROFILE} aws cloudformation describe-stacks \
      --stack-name "${pattern}" \
      --query 'Stacks[?StackStatus==`CREATE_COMPLETE` || StackStatus==`UPDATE_COMPLETE`].StackName' \
      --output text 2>/dev/null | grep -q "${pattern}"; then
      echo "$pattern"
      return 0
    fi
  done

  echo ""
  return 1
}

# Get OpenSearch endpoint
echo "1. Retrieving OpenSearch endpoint..."
STACK_NAME=$(detect_opensearch_stack "${ENVIRONMENT}" "${DEVELOPER_NAME}")

if [ -z "$STACK_NAME" ]; then
  echo "ERROR: Could not find OpenSearch stack"
  echo ""
  echo "Tried stacks:"
  if [ -n "$DEVELOPER_NAME" ]; then
    echo "  - openchallenges-${ENVIRONMENT}-${DEVELOPER_NAME}-opensearch"
  fi
  echo "  - openchallenges-${ENVIRONMENT}-opensearch"
  if [ "${ENVIRONMENT}" = "dev" ] && [ -z "$DEVELOPER_NAME" ]; then
    echo "  - openchallenges-${ENVIRONMENT}-$(whoami)-opensearch"
  fi
  echo ""
  echo "Available stacks:"
  AWS_PROFILE=${AWS_PROFILE} aws cloudformation list-stacks \
    --stack-status-filter CREATE_COMPLETE UPDATE_COMPLETE \
    --query "StackSummaries[?contains(StackName, 'openchallenges')].StackName" \
    --output text | tr '\t' '\n' | sed 's/^/  - /'
  exit 1
fi

echo "Found stack: ${STACK_NAME}"

OPENSEARCH_ENDPOINT=$(AWS_PROFILE=${AWS_PROFILE} aws cloudformation describe-stacks \
  --stack-name "${STACK_NAME}" \
  --query 'Stacks[0].Outputs[?OutputKey==`DomainEndpoint`].OutputValue' \
  --output text)

if [ -z "$OPENSEARCH_ENDPOINT" ] || [ "$OPENSEARCH_ENDPOINT" = "None" ]; then
  echo "ERROR: Could not retrieve OpenSearch endpoint from stack ${STACK_NAME}"
  exit 1
fi

echo "OpenSearch endpoint: ${OPENSEARCH_ENDPOINT}"
echo ""

# Function to detect OpenSearch credentials secret
detect_opensearch_secret() {
  local env=$1
  local dev_name=$2

  # Build list of patterns to try
  local patterns=()

  if [ -n "$dev_name" ]; then
    # Try with developer name first
    patterns+=("openchallenges-${env}-${dev_name}-opensearch-credentials")
  fi

  # Try without developer name
  patterns+=("openchallenges-${env}-opensearch-credentials")

  # Try with current username as fallback for dev
  if [ "$env" = "dev" ] && [ -z "$dev_name" ]; then
    patterns+=("openchallenges-${env}-$(whoami)-opensearch-credentials")
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

# Get OpenSearch credentials
echo "2. Retrieving OpenSearch credentials..."
SECRET_ARN=$(detect_opensearch_secret "${ENVIRONMENT}" "${DEVELOPER_NAME}")

if [ -z "$SECRET_ARN" ]; then
  echo "ERROR: Could not find OpenSearch credentials secret"
  echo ""
  echo "Tried patterns:"
  if [ -n "$DEVELOPER_NAME" ]; then
    echo "  - openchallenges-${ENVIRONMENT}-${DEVELOPER_NAME}-opensearch-credentials"
  fi
  echo "  - openchallenges-${ENVIRONMENT}-opensearch-credentials"
  if [ "${ENVIRONMENT}" = "dev" ] && [ -z "$DEVELOPER_NAME" ]; then
    echo "  - openchallenges-${ENVIRONMENT}-$(whoami)-opensearch-credentials"
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

OPENSEARCH_USER=$(echo "$SECRET_JSON" | python3 -c "import sys, json; print(json.load(sys.stdin)['username'])")
OPENSEARCH_PASSWORD=$(echo "$SECRET_JSON" | python3 -c "import sys, json; print(json.load(sys.stdin)['password'])")

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
echo "3. Finding ECS cluster and bastion service..."
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
echo "4. Getting task details..."
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
echo "5. Starting port forwarding session..."
echo "Forwarding localhost:${LOCAL_PORT} -> ${OPENSEARCH_ENDPOINT}:443"
echo ""
echo "=== Port Forwarding Active ==="
echo ""
echo "You can now access OpenSearch Dashboards at:"
echo "  https://localhost:${LOCAL_PORT}/_dashboards"
echo ""
echo "Or use the OpenSearch API:"
echo "  curl -k -u ${OPENSEARCH_USER}:${OPENSEARCH_PASSWORD} https://localhost:${LOCAL_PORT}/"
echo ""
echo "Credentials:"
echo "  Username: ${OPENSEARCH_USER}"
echo "  Password: ${OPENSEARCH_PASSWORD}"
echo ""
echo "Note: You may need to accept the self-signed certificate warning in your browser."
echo ""
echo "Press Ctrl+C to stop port forwarding"
echo ""

# Start Session Manager port forwarding to remote host (OpenSearch) through bastion
AWS_PROFILE=${AWS_PROFILE} aws ssm start-session \
  --target "ecs:${CLUSTER_NAME}_${TASK_ID}_${RUNTIME_ID}" \
  --document-name AWS-StartPortForwardingSessionToRemoteHost \
  --parameters "{\"host\":[\"${OPENSEARCH_ENDPOINT}\"],\"portNumber\":[\"443\"],\"localPortNumber\":[\"${LOCAL_PORT}\"]}"

echo ""
echo "Port forwarding closed."
