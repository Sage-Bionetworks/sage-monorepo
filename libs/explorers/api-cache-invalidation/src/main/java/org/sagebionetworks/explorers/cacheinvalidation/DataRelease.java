package org.sagebionetworks.explorers.cacheinvalidation;

/**
 * @param dataFile Synapse ID of the data file, e.g. `syn73774820`
 * @param dataVersion Synapse version number of the data file
 */
public record DataRelease(String dataFile, String dataVersion) {
  @Override
  public String toString() {
    return dataFile + "-v" + dataVersion;
  }
}
