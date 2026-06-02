package org.sagebionetworks.explorers.cacheinvalidation;

import java.util.Optional;

@FunctionalInterface
public interface DataVersionProvider {
  Optional<String> getCurrentDataVersion();
}
