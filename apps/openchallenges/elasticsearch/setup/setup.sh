#!/usr/bin/env bash

# Requires a Docker volume mounted to `/certs`.

if [ ! -f /certs/ca.zip ]; then
  echo "Creating CA";
  bin/elasticsearch-certutil ca --silent --pem -out /certs/ca.zip;
  unzip /certs/ca.zip -d /certs;
fi;
if [ ! -f /certs/certs.zip ]; then
  echo "Creating certs";
  bin/elasticsearch-certutil cert --silent --pem -out /certs/certs.zip --in config/certs/instances.yml --ca-cert /certs/ca/ca.crt --ca-key /certs/ca/ca.key;
  unzip /certs/certs.zip -d /certs;
fi;
echo "Setting file permissions"
chown -R root:root /certs;
find /certs -type d -exec chmod 750 \{\} \;;
find /certs -type f -exec chmod 640 \{\} \;;
echo "Waiting for Elasticsearch availability";
until curl -s --cacert /certs/ca/ca.crt https://openchallenges-elasticsearch:9200 | grep -q "missing authentication credentials"; do sleep 30; done;
echo "Setting kibana_system password";
until curl -s -X POST --cacert /certs/ca/ca.crt -u "elastic:${ELASTIC_PASSWORD}" -H "Content-Type: application/json" https://openchallenges-elasticsearch:9200/_security/user/kibana_system/_password -d "{\"password\":\"${KIBANA_PASSWORD}\"}" | grep -q "^{}"; do sleep 10; done;
echo "Done";