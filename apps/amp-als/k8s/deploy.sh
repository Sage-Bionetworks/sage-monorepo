#!/bin/bash

# AMP-ALS Kubernetes Deployment Script
# This script deploys the AMP-ALS stack to a Kubernetes cluster

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
NAMESPACE="amp-als"
DEPLOYMENT_MODE="kubectl"
IMAGE_TAG="local"

# Functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

show_help() {
    echo "AMP-ALS Kubernetes Deployment Script"
    echo ""
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help                Show this help message"
    echo "  -m, --mode MODE          Deployment mode: kubectl or helm (default: kubectl)"
    echo "  -n, --namespace NAME     Kubernetes namespace (default: amp-als)"
    echo "  -t, --tag TAG           Docker image tag (default: local)"
    echo "  --cleanup               Remove all deployed resources"
    echo ""
    echo "Examples:"
    echo "  $0                      Deploy with kubectl"
    echo "  $0 -m helm             Deploy with Helm"
    echo "  $0 --cleanup            Remove all resources"
}

check_prerequisites() {
    log_info "Checking prerequisites..."

    # Check kubectl
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl is not installed or not in PATH"
        exit 1
    fi

    # Check if kubectl can connect to cluster
    if ! kubectl cluster-info &> /dev/null; then
        log_error "Cannot connect to Kubernetes cluster. Make sure your cluster is running and kubectl is configured."
        exit 1
    fi

    # Check Helm if using Helm mode
    if [[ "$DEPLOYMENT_MODE" == "helm" ]] && ! command -v helm &> /dev/null; then
        log_error "Helm is not installed or not in PATH"
        exit 1
    fi

    log_success "Prerequisites check passed"
}

deploy_with_kubectl() {
    log_info "Deploying AMP-ALS with kubectl..."

    # Create namespace
    log_info "Creating namespace: $NAMESPACE"
    kubectl apply -f namespace.yaml

    # Deploy secrets
    log_info "Deploying secrets..."
    kubectl apply -f secrets/

    # Deploy configmaps
    log_info "Deploying configmaps..."
    kubectl apply -f configmaps/

    # Deploy PostgreSQL
    log_info "Deploying PostgreSQL..."
    kubectl apply -f postgres/

    # Wait for PostgreSQL to be ready
    log_info "Waiting for PostgreSQL to be ready..."
    kubectl wait --for=condition=ready pod -l app=amp-als-postgres -n "$NAMESPACE" --timeout=300s

    # Deploy Dataset Service
    log_info "Deploying Dataset Service..."
    kubectl apply -f dataset-service/

    # Wait for Dataset Service to be ready
    log_info "Waiting for Dataset Service to be ready..."
    kubectl wait --for=condition=ready pod -l app=amp-als-dataset-service -n "$NAMESPACE" --timeout=300s

    log_success "Deployment completed successfully!"
}

deploy_with_helm() {
    log_info "Deploying AMP-ALS with Helm..."

    helm install amp-als ./helm \
        --namespace "$NAMESPACE" \
        --create-namespace \
        --set global.imageTag="$IMAGE_TAG"

    log_success "Helm deployment completed successfully!"
}

cleanup_resources() {
    log_warning "Cleaning up AMP-ALS resources..."

    if [[ "$DEPLOYMENT_MODE" == "helm" ]]; then
        log_info "Uninstalling Helm release..."
        helm uninstall amp-als -n "$NAMESPACE" || true
    fi

    log_info "Deleting namespace: $NAMESPACE"
    kubectl delete namespace "$NAMESPACE" --ignore-not-found=true

    log_success "Cleanup completed!"
}

show_access_info() {
    log_info "Access Information:"
    echo ""
    echo "Dataset Service:"
    echo "  Internal URL: http://amp-als-dataset-service.${NAMESPACE}.svc.cluster.local:8404"
    echo ""
    echo "To access externally:"
    echo "  # Port forward:"
    echo "  kubectl port-forward service/amp-als-dataset-service 8404:8404 -n $NAMESPACE"
    echo "  # Then access: http://localhost:8404"
    echo ""
    echo "  # Or via NodePort (if using minikube):"
    echo "  minikube service amp-als-dataset-service-nodeport -n $NAMESPACE"
    echo ""
    echo "PostgreSQL (for debugging):"
    echo "  kubectl port-forward service/amp-als-postgres 8401:8401 -n $NAMESPACE"
    echo "  psql -h localhost -p 8401 -U dataset_service -d dataset_service"
}

show_status() {
    log_info "Deployment Status:"
    echo ""
    kubectl get pods -n "$NAMESPACE"
    echo ""
    kubectl get services -n "$NAMESPACE"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -m|--mode)
            DEPLOYMENT_MODE="$2"
            shift 2
            ;;
        -n|--namespace)
            NAMESPACE="$2"
            shift 2
            ;;
        -t|--tag)
            IMAGE_TAG="$2"
            shift 2
            ;;
        --cleanup)
            cleanup_resources
            exit 0
            ;;
        *)
            log_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

# Validate deployment mode
if [[ "$DEPLOYMENT_MODE" != "kubectl" && "$DEPLOYMENT_MODE" != "helm" ]]; then
    log_error "Invalid deployment mode: $DEPLOYMENT_MODE. Use 'kubectl' or 'helm'"
    exit 1
fi

# Main execution
log_info "Starting AMP-ALS deployment..."
log_info "Mode: $DEPLOYMENT_MODE"
log_info "Namespace: $NAMESPACE"
log_info "Image Tag: $IMAGE_TAG"

check_prerequisites

case $DEPLOYMENT_MODE in
    kubectl)
        deploy_with_kubectl
        ;;
    helm)
        deploy_with_helm
        ;;
esac

show_status
show_access_info
