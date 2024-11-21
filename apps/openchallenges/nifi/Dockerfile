FROM apache/nifi:2.0.0

ENV MYSQL_CONNECTOR_VERSION=9.1.0
ENV MYSQL_CONNECTOR_URL=https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}.tar.gz
ENV MARIADB_CONNECTOR_VERSION=3.5.0
# The integer ID in the URL below is unique to each artifact hosted on dlm.mariadb.com.
ENV MARIADB_CONNECTOR_URL=https://dlm.mariadb.com/3934032/Connectors/java/connector-java-${MARIADB_CONNECTOR_VERSION}/mariadb-java-client-${MARIADB_CONNECTOR_VERSION}.jar

# Create the drivers directory
RUN mkdir -p /opt/nifi/nifi-current/drivers/

# Download and extract the MySQL JDBC connector, then move the JAR to the drivers directory
RUN curl -L -o /tmp/mysql-connector-j.tar.gz "${MYSQL_CONNECTOR_URL}" \
    && tar -xzf /tmp/mysql-connector-j.tar.gz -C /tmp \
    && mv /tmp/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}.jar /opt/nifi/nifi-current/drivers/ \
    && rm -rf /tmp/mysql-connector-j.tar.gz /tmp/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}

# Download the MariaDB JDBC connector and place it in the drivers directory
RUN curl -L -o "/opt/nifi/nifi-current/drivers/mariadb-java-client-${MARIADB_CONNECTOR_VERSION}.jar" \
    "${MARIADB_CONNECTOR_URL}"