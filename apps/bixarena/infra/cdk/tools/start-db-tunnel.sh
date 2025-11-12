#!/bin/bash
# Script to establish port forwarding to RDS via bastion ECS service
# Usage: ./start-db-tunnel.sh [dev|stage|prod] [developer-name]

set -e

# Configuration
ENVIRONMENT=${1:-dev}
DEVELOPER_NAME=${2:-$(whoami)}
AWS_PROFILE="bixarena-${ENVIRONMENT}-Developer"
LOCAL_PORT=5432

echo "=== BixArena Database Port Forwarding ==="
echo "Environment: ${ENVIRONMENT}"
echo "Developer: ${DEVELOPER_NAME}"
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

# Get database credentials
echo "1. Retrieving database credentials..."
SECRET_PATTERN="bixarena-${ENVIRONMENT}-${DEVELOPER_NAME}-database"
SECRET_ARN=$(AWS_PROFILE=${AWS_PROFILE} aws secretsmanager list-secrets \
  --query "SecretList[?contains(Name, \`${SECRET_PATTERN}\`)].ARN" \
  --output text)

if [ -z "$SECRET_ARN" ]; then
  echo "ERROR: Could not find database secret"
  exit 1
fi

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

# Find the cluster
echo "2. Finding ECS cluster and bastion service..."
STACK_PREFIX="bixarena-${ENVIRONMENT}-${DEVELOPER_NAME}"
CLUSTER_NAME="${STACK_PREFIX}"

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
  echo "ERROR: No running bastion task found"
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
  --query 'tasks[0].containers[0].runtimeId' \
  --output text)

if [ -z "$RUNTIME_ID" ] || [ "$RUNTIME_ID" = "None" ]; then
  echo "ERROR: Could not get container runtime ID"
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
