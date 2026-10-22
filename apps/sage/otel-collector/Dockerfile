FROM mirror.gcr.io/amazon/aws-otel-collector:v0.41.1 as aws-otel

FROM mirror.gcr.io/otel/opentelemetry-collector-contrib:0.113.0

COPY --from=aws-otel /healthcheck /healthcheck
