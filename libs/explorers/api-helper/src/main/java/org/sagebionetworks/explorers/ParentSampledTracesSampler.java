package org.sagebionetworks.explorers;

import io.sentry.SamplingContext;
import io.sentry.SentryOptions;

/**
 * Traces a request only when it continues a browser trace, following that trace's sampling
 * decision so traces stay whole. A request with no incoming trace, such as a server-side rendering
 * call, a load test, or a direct API call, is not traced, so it uses no span quota.
 */
public final class ParentSampledTracesSampler implements SentryOptions.TracesSamplerCallback {

  static final double NO_PARENT_SAMPLE_RATE = 0.0;

  @Override
  public Double sample(final SamplingContext samplingContext) {
    // Returning null leaves the decision to the SDK, which then inherits the parent's decision.
    // Without a parent the SDK would fall back to the configured traces-sample-rate instead.
    return samplingContext.getTransactionContext().getParentSamplingDecision() != null
      ? null
      : NO_PARENT_SAMPLE_RATE;
  }
}
