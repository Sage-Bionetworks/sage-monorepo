FROM mongo:6.0.3

ARG agoraDatasetVersion="1.0.0" \
    synapseUsername="" \
    synapseToken=""

COPY scripts ./scripts

RUN bash /scripts/download-data-from-synapse.sh

RUN bash /scripts/export-data-to-mongo.sh