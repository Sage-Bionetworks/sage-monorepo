#!/usr/bin/env python3
"""
Example script instrumented with OpenTelemetry to send telemetry data to the Sage observability stack.

This script demonstrates how to:
1. Configure OpenTelemetry logging, metrics, and traces
2. Instrument a script to send data to the observability stack

Requires:
- opentelemetry-api
- opentelemetry-sdk
- opentelemetry-exporter-otlp
- opentelemetry-instrumentation
"""

import argparse
import logging
import os
import sys
import time
import random
from functools import wraps

# OpenTelemetry imports
from opentelemetry import trace, metrics
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor
from opentelemetry.sdk.metrics import MeterProvider
from opentelemetry.sdk.metrics.export import PeriodicExportingMetricReader
from opentelemetry.sdk.resources import Resource
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from opentelemetry.exporter.otlp.proto.grpc.metric_exporter import OTLPMetricExporter
from opentelemetry.instrumentation.logging import LoggingInstrumentor

# No profiling functionality

# Constants
SERVICE_NAME = "agora-data"
SERVICE_VERSION = "0.1.0"

# Setup the OpenTelemetry resources
resource = Resource.create(
    {
        "service.name": SERVICE_NAME,
        "service.version": SERVICE_VERSION,
    }
)


# Configure Logging with OpenTelemetry
def setup_logging():
    # Create a logger
    logger = logging.getLogger(SERVICE_NAME)
    logger.setLevel(logging.INFO)

    # Configure standard output handler
    handler = logging.StreamHandler()
    formatter = logging.Formatter(
        "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
    )
    handler.setFormatter(formatter)
    logger.addHandler(handler)

    # Instrument logging with OpenTelemetry
    LoggingInstrumentor().instrument(set_logging_format=True)

    return logger


# Configure Tracing with OpenTelemetry
def setup_tracing():
    # Create a tracer provider
    tracer_provider = TracerProvider(resource=resource)

    # Create an OTLP exporter and add it to the tracer provider
    otlp_exporter = OTLPSpanExporter(
        endpoint="http://observability-otel-collector:8508",  # gRPC endpoint
        insecure=True,
    )
    span_processor = BatchSpanProcessor(otlp_exporter)
    tracer_provider.add_span_processor(span_processor)

    # Set the tracer provider
    trace.set_tracer_provider(tracer_provider)

    # Get a tracer
    tracer = trace.get_tracer(SERVICE_NAME, SERVICE_VERSION)

    return tracer


# Configure Metrics with OpenTelemetry
def setup_metrics():
    # Create a metric reader
    metric_reader = PeriodicExportingMetricReader(
        OTLPMetricExporter(
            endpoint="http://observability-otel-collector:8508",  # gRPC endpoint
            insecure=True,
        ),
        export_interval_millis=10000,  # 10 seconds
    )

    # Create a meter provider
    meter_provider = MeterProvider(resource=resource, metric_readers=[metric_reader])

    # Set the meter provider
    metrics.set_meter_provider(meter_provider)

    # Get a meter
    meter = metrics.get_meter(SERVICE_NAME, SERVICE_VERSION)

    # Create some metrics
    request_counter = meter.create_counter(
        name="requests",
        description="Number of requests processed",
        unit="1",
    )

    request_duration = meter.create_histogram(
        name="request_duration",
        description="Duration of requests",
        unit="ms",
    )

    return request_counter, request_duration


# No profiling functionality


# Tracing decorator for functions
def trace_function(tracer):
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            with tracer.start_as_current_span(func.__name__):
                return func(*args, **kwargs)

        return wrapper

    return decorator


# Main function to simulate work
def main():
    # Setup telemetry
    logger = setup_logging()
    tracer = setup_tracing()
    request_counter, request_duration = setup_metrics()

    # Log initialization
    logger.info(f"Starting {SERVICE_NAME} v{SERVICE_VERSION}")
    logger.info(f"Tracing enabled: {tracer is not None}")
    logger.info(
        f"Metrics enabled: {request_counter is not None and request_duration is not None}"
    )

    # Parse arguments
    parser = argparse.ArgumentParser(
        description="Script with OpenTelemetry instrumentation"
    )
    parser.add_argument(
        "--iterations", type=int, default=10, help="Number of iterations to run"
    )
    args = parser.parse_args()

    # Simulate work
    @trace_function(tracer)
    def process_item(item_id):
        logger.info(f"Processing item {item_id}")
        # Simulate some work
        sleep_time = random.uniform(0.1, 0.5)
        time.sleep(sleep_time)

        # Record metrics
        request_counter.add(1, {"item_id": str(item_id)})
        request_duration.record(sleep_time * 1000, {"item_id": str(item_id)})

        # Sometimes generate an error
        if random.random() < 0.2:
            try:
                raise ValueError(f"Simulated error processing item {item_id}")
            except ValueError as e:
                logger.error(f"Error: {e}", exc_info=True)

        return sleep_time

    # Main processing loop
    with tracer.start_as_current_span("main_processing"):
        total_time = 0
        for i in range(args.iterations):
            try:
                processing_time = process_item(i)
                total_time += processing_time
                logger.info(f"Completed item {i} in {processing_time:.2f} seconds")
            except Exception as e:
                logger.error(f"Unhandled exception: {e}", exc_info=True)

        logger.info(f"Processed {args.iterations} items in {total_time:.2f} seconds")

    logger.info(f"Shutting down {SERVICE_NAME}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
