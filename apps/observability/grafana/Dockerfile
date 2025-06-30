FROM mirror.gcr.io/grafana/grafana:12.0.0

# Copy datasources
COPY provisioning/datasources/ /etc/grafana/provisioning/datasources/

# Copy dashboard provisioning configuration
COPY provisioning/dashboards/ /etc/grafana/provisioning/dashboards/

# Copy dashboards
COPY dashboards/*.json /etc/grafana/dashboards/