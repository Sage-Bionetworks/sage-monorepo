# Purpose
As discussed in this Github Issue: <https://github.com/open-telemetry/opentelemetry-collector-contrib/issues/30798#issuecomment-2009233014>
The official opentelemetry (OTEL) collector image does not contain cURL or related shell
commands required to do container level health checks. It is reliant on external
services such as the application load balancer in AWS to perform these checks. This is
problematic with our deployment of the OTEL collector as we are using AWS
service connect with AWS ECS to allow other containers within the namespace to connect 
to the collector. As such, there is no load balancer in-front of the container to handle 
its lifecycle. Within ECS, the recommended way from AWS to handle container level health
checks is to let ECS perform commands in the container. 
Source: <https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_HealthCheck.html>


Since the OTEL collector does not have a shell, nor cURL available we need to accomplish
this another way. In the official AWS OTEL collector distro they accomplish this by
compiling a golang script down into a binary that can be run within the container.
Unfortunately we cannot use the AWS OTEL collector because they are not supporting the
`oauth2clientauthextension`: <https://github.com/aws-observability/aws-otel-collector/issues/1492>.


For our purposes we are creating a new image based off the `otel/opentelemetry-collector-contrib` image,
but with the addition of the healthcheck binary from the AWS OTEL distro. This
combination lets us use the oauth2 extension, and have container level health checks.


## Creating a new image (To automate later on)
As new base images are updated we will need to in-turn create a new otel collector
image that we deploy to ECS.

1) Update values in the `Dockerfile`
2) Run `docker build -t ghcr.io/sage-bionetworks/sage-otel-collector:vX.X.X .` (Replace the version)
3) Run `docker push ghcr.io/sage-bionetworks/sage-otel-collector:vX.X.X` (Replace the version)

Once a new image is built and pushed, then you'll want to update the values in the CDK
scripts to use the new image version.
