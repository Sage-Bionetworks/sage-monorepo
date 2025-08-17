package org.sagebionetworks.bixarena.api.model.projection;

import java.time.OffsetDateTime;

public interface SnapshotWithEntryCount {
  String getId();
  String getSnapshotIdentifier();
  String getDescription();
  OffsetDateTime getCreatedAt();
  Long getEntryCount();
}
