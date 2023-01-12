package org.sagebionetworks.challenge.exception;

public class SortingStrategyNotImplemented extends SimpleChallengeGlobalException {
  public SortingStrategyNotImplemented(String detail) {
    super(
        ErrorConstants.SORTING_STRATEGY_NOT_IMPLEMENTED.getType(),
        ErrorConstants.SORTING_STRATEGY_NOT_IMPLEMENTED.getTitle(),
        ErrorConstants.SORTING_STRATEGY_NOT_IMPLEMENTED.getStatus(),
        detail);
  }
}
