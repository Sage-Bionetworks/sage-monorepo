FROM public.ecr.aws/aws-observability/aws-otel-collector:v0.41.1 as aws-otel

FROM otel/opentelemetry-collector-contrib:0.113.0

COPY --from=aws-otel /healthcheck /healthcheck
