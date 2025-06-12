FROM grafana/grafana:12.0.0

# Copy datasources
COPY datasources/ /etc/grafana/provisioning/datasources/

# Copy dashboard provisioning configuration
COPY dashboards/dashboard.yml /etc/grafana/provisioning/dashboards/

# Copy dashboards
COPY dashboards/*.json /etc/grafana/dashboards/