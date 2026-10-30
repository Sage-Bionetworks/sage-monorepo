package org.sagebionetworks.explorers.cacheinvalidation;

/**
 * The tracker's state, as reported in the API's health details.
 *
 * @param release the release in use, e.g. `syn13363290-v117`, or null if none has been read
 * @param lastRefreshedAt when the release was last read successfully, in ISO-8601, or null if never
 * @param consecutiveFailures how many refreshes in a row have failed
 */
public record DataReleaseState(String release, String lastRefreshedAt, int consecutiveFailures) {}
