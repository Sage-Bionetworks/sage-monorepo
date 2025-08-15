# AMP-ALS Kubernetes Deployment

This directory contains Kubernetes manifests and Helm chart for deploying the AMP-ALS stack.

## Components

- **PostgreSQL**: Database service with persistent storage
- **Dataset Service**: Spring Boot application for dataset management
- **OpenSearch**: Search and analytics engine

## Prerequisites

- Kubernetes cluster (minikube for local development)
- kubectl CLI tool
- Helm 3.x (optional, for Helm deployment)

## Quick Start

### 1. Start Minikube (for local development)

```bash
minikube start
```

### 2. Deploy with kubectl

```bash
# Create namespace
kubectl apply -f namespace.yaml

# Deploy PostgreSQL
kubectl apply -f secrets/
kubectl apply -f configmaps/
kubectl apply -f postgres/

# Wait for PostgreSQL to be ready
kubectl wait --for=condition=ready pod -l app=amp-als-postgres -n amp-als --timeout=300s

# Deploy OpenSearch
kubectl apply -f opensearch/

# Wait for OpenSearch to be ready
kubectl wait --for=condition=ready pod -l app=amp-als-opensearch -n amp-als --timeout=300s

# Deploy Dataset Service
kubectl apply -f dataset-service/
```

### 3. Deploy with Helm (Alternative)

```bash
# Install with Helm
helm install amp-als ./helm -n amp-als --create-namespace
```

## Access the Services

### Dataset Service

```bash
# Port forward to access locally
kubectl port-forward service/amp-als-dataset-service 8404:8404 -n amp-als

# Access at: http://localhost:8404
```

### PostgreSQL (for debugging)

```bash
# Port forward to access locally
kubectl port-forward service/amp-als-postgres 8401:8401 -n amp-als

# Connect with psql client
psql -h localhost -p 8401 -U dataset_service -d dataset_service
```

### OpenSearch (for debugging)

```bash
# Port forward to access locally
kubectl port-forward service/amp-als-opensearch 8402:8402 -n amp-als

# Access at: http://localhost:8402
```

## Scaling

```bash
# Scale dataset service
kubectl scale deployment amp-als-dataset-service --replicas=3 -n amp-als
```

## Cleanup

```bash
# Remove all resources
kubectl delete namespace amp-als

# Or with Helm
helm uninstall amp-als -n amp-als
```

## Configuration

- Environment variables are stored in ConfigMaps and Secrets
- Persistent storage is used for PostgreSQL data
- Services are exposed via ClusterIP by default

## Troubleshooting

```bash
# Check pod status
kubectl get pods -n amp-als

# Check logs
kubectl logs -f deployment/amp-als-dataset-service -n amp-als
kubectl logs -f deployment/amp-als-postgres -n amp-als

# Describe resources for more details
kubectl describe pod <pod-name> -n amp-als
```
