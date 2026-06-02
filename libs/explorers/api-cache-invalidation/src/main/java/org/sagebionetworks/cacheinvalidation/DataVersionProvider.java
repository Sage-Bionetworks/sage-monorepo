package org.sagebionetworks.cacheinvalidation;

import java.util.Optional;

@FunctionalInterface
public interface DataVersionProvider {
  Optional<String> getCurrentDataVersion();
}
