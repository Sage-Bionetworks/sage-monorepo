package org.sagebionetworks.explorers.cacheinvalidation;

/**
 * Identifies a data release by both fields of the `dataversion` document, since a release can
 * change the data file without changing the version number.
 */
public record DataRelease(String dataFile, String dataVersion) {
  @Override
  public String toString() {
    return dataFile + "-v" + dataVersion;
  }
}
