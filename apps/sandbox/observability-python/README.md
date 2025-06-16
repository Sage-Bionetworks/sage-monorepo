# Sandbox Observability

## Description

Develop a sample Python project that implements the "three pillars of observability" plus profiling
to serves as a reference implementation for properly instrumenting Python applications using modern
observability stacks, such as the OTEL-Grafana stack available in the Sage monorepo.

## Start the Observability stack

```bash
observability-build-images
observability-docker-start
```

## Prepare the Python project

```bash
nx run-many -p sandbox-observability-python -t create-config prepare
```

## Run the Python script with Docker Compose

The script creates a simulated workload by:

- Running a configurable number of iterations (defaults to 10)
- Processing items with a mix of CPU-intensive tasks (Fibonacci calculations) and simulated delays
- Occasionally generating errors (20% chance per item) to demonstrate error tracking

```bash
nx build-image sandbox-observability-python
nx serve sandbox-observability-python
```

> [!NOTE]
> The Pyroscope Python package fails to find the Python binary when running the script with `uv`
> because the Python binary exists in a virtual environment. The workaround implemented here is to
> run the script with Docker Compose with a Docker image that run the script directly with Python
> (without using `uv`).

> [!NOTE]
> The `src/` folder is mounted as a Docker volume, enabling the script to run via Docker Compose
> without rebuilding the image first, which improves the developer experience.

## Observability Implementation

The script implements the "three pillars of observability" plus profiling:

1. Metrics

- Uses OpenTelemetry metrics API to track:
  - Request counts with attributes
  - Request durations as histograms
  - Application runtime
  - An "up" metric for service health monitoring
- Exports metrics to a Prometheus-compatible endpoint via the OTEL collector

2. Logging

- Configures comprehensive structured logging
- Instruments Python's standard logging module with OpenTelemetry
- Exports logs to Loki (via OTEL collector) for centralized log aggregation
- Includes contextual information like service name and version

3. Tracing

- Creates distributed traces for operations
- Implements a decorator for automatic function tracing
- Captures parent-child span relationships for the processing pipeline
- Exports traces to Tempo (via OTEL collector)

5. Profiling

- Integrates with Pyroscope for CPU profiling
- Uses custom tagging to segment profiles by operation
- Collects fine-grained performance data during execution

Technical Implementation:

- Uses gRPC for efficient communication with the OTEL collector
- Ensures clean shutdown with metric flushing and profiler cleanup
- Provides proper resource attribution in the observability backend
- Demonstrates error handling throughout the telemetry pipeline
- This script serves as a reference implementation for how to properly instrument Python
  applications for modern observability stacks like the Grafana LGTM stack (Loki, Grafana, Tempo,
  Mimir).

## Login into Grafana

1. In a browser, navigate to the Grafana web UI at http://localhost:8501.
2. Log in using the default credentials: admin / admin.
3. Click Skip to bypass setting new credentials.

## Access the Profiles

A flame graph is a visualization tool used in Grafana (here with profiling data from Pyroscope) to
display application performance and resource usage, particularly CPU or memory consumption over
time.

It presents a hierarchical stack of function calls where:

- The x-axis shows the call stack horizontally (not time).
- The width of each box represents the amount of time (or resources) a function and its children
  consumed.
- The y-axis shows the call depth, with parent functions at the bottom and child calls above.

This helps developers and performance engineers:

- Identify performance bottlenecks.
- Understand which functions consume the most resources.
- Drill down into hot paths in the code.

Flame graphs are essential for profiling complex systems and optimizing runtime behavior.

1. In the left-hand menu, go to Drilldown> Profiles.
2. Next to "Exploration", click on the button "Flame graph".
3. Select the "sandbox-observability-python" service.
4. The flame graph should appear.
   - If not, check the value of the time filter (e.g., "Last 15 minutes").

Among other information, the flame graph shows how much time was spent by the Python script in the
different named functions.

![image](https://github.com/user-attachments/assets/1cb1381a-b982-46ad-8251-14cbf5dcd27d)

## Access the Logs

1. In the left-hand menu, go to Drilldown> Logs.
2. You should see logs listed.
   - If not, check the value of "Data source" (Loki) and the time filter (e.g., "Last 15 minutes").

![image](https://github.com/user-attachments/assets/3c27ab98-6aea-48d0-83a3-eaeb52c14313)

## Access the Metrics

Metrics in Grafana are time-series data that represent how a system or application behaves over
time. They are typically collected from sources like Prometheus, Graphite, InfluxDB, and others, and
are used to track quantitative performance indicators such as:

- CPU and memory usage
- Request rates and latencies
- Error counts
- Disk I/O and network traffic

Grafana enables users to:

- Query, visualize, and alert on metrics with flexible dashboards.
- Use graphs, gauges, heatmaps, and other visualizations to detect trends and anomalies.
- Set up threshold-based alerts to trigger notifications for unusual system behavior.
- Correlate metrics with logs and traces for full observability.

Metrics are essential for monitoring system health, conducting capacity planning, and performing
incident response in real time.

1. In the left-hand menu, go to Drilldown> Metrics.
2. You should see metrics listed, which have names starting with "sandbox*observability_python*".
   - If not, check the value of the time filter (e.g., "Last 15 minutes").

![image](https://github.com/user-attachments/assets/4fc92345-22b3-4ce4-95d7-fc59de58ed16)

## Access additional Metrics in Custom Dashboard

This PR provides a custom Grafana dashboard that compute new metrics based on the metrics exported
by the Python script.

1. In the left-hand menu, go to Dashboards.
2. Click on the "Python Observability" dashboard.
3. Data should appear in at least two plots.
   - If not, check the value of the time filter (e.g., "Last 15 minutes").

![image](https://github.com/user-attachments/assets/f3c754b8-b4df-49ff-bb5d-8934c5f1991e)

## Access the Traces

Traces in Grafana represent a chronological view of events that occur during a single transaction or
request as it flows through a distributed system. Each trace is made up of spans, where each span
records a specific operation or step (e.g., a function call, HTTP request, or database query).

When integrated with a backend like Grafana Tempo, which we use here, traces help you:

- Visualize end-to-end request flows across microservices.
- Identify performance bottlenecks and slow components.
- Understand service dependencies and latencies.
- Debug failures and anomalies in complex systems.

Traces are typically visualized in a waterfall view, showing how operations nest and how long each
one takes, making them essential for observability and performance analysis in distributed
applications.

1. In the left-hand menu, go to Explore (Drilldown > Traces doesn't work presently).
2. Select "Tempo" as the data source.
3. Set the "Query type" to "Search".
4. A trace for the "sandbox-observability-python" service should be listed.
   - If not, check the value of the time filter (e.g., "Last 15 minutes").
5. Click on its Trace ID to show more details.

![image](https://github.com/user-attachments/assets/32196d0b-7952-4947-aaa5-163bcb0f91c0)

![image](https://github.com/user-attachments/assets/2f816f7b-15d2-4654-b70a-d4aa715ba60f)

## Improvements

The instrumentation code can be extracted from the Python script and moved into a shared Python
library, allowing other projects within the monorepo to reuse it. Creating application or library
projects that depend on local Python packages in the monorepo has been successfully done before.

[SMR-175]: https://sagebionetworks.jira.com/browse/SMR-175?atlOrigin=eyJpIjoiNWRkNTljNzYxNjVmNDY3MDlhMDU5Y2ZhYzA5YTRkZjUiLCJwIjoiZ2l0aHViLWNvbS1KU1cifQ
