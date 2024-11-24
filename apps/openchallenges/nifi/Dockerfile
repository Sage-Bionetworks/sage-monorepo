FROM apache/nifi:2.0.0

ENV MYSQL_CONNECTOR_VERSION=9.1.0
ENV MYSQL_CONNECTOR_URL=https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}.tar.gz

# Create the drivers directory
RUN mkdir -p /opt/nifi/nifi-current/drivers/

# Download and extract the MySQL JDBC connector, then move the JAR to the drivers directory
RUN curl -L -o /tmp/mysql-connector-j.tar.gz "${MYSQL_CONNECTOR_URL}" \
    && tar -xzf /tmp/mysql-connector-j.tar.gz -C /tmp \
    && mv /tmp/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}.jar /opt/nifi/nifi-current/drivers/ \
    && rm -rf /tmp/mysql-connector-j.tar.gz /tmp/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}