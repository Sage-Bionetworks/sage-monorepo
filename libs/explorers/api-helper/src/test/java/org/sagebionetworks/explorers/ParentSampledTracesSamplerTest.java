package org.sagebionetworks.explorers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.sentry.SamplingContext;
import io.sentry.TracesSamplingDecision;
import io.sentry.TransactionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParentSampledTracesSamplerTest {

  @Mock
  private SamplingContext samplingContext;

  @Mock
  private TransactionContext transactionContext;

  private final ParentSampledTracesSampler sampler = new ParentSampledTracesSampler();

  @BeforeEach
  void setUp() {
    when(samplingContext.getTransactionContext()).thenReturn(transactionContext);
  }

  @Test
  @DisplayName("should leave the decision to the sdk when the request continues a browser trace")
  void shouldLeaveTheDecisionToTheSdkWhenTheRequestContinuesABrowserTrace() {
    when(transactionContext.getParentSamplingDecision()).thenReturn(
      new TracesSamplingDecision(true)
    );

    assertThat(sampler.sample(samplingContext)).isNull();
  }

  @Test
  @DisplayName("should not trace when the request has no incoming trace")
  void shouldNotTraceWhenTheRequestHasNoIncomingTrace() {
    when(transactionContext.getParentSamplingDecision()).thenReturn(null);

    assertThat(sampler.sample(samplingContext)).isEqualTo(
      ParentSampledTracesSampler.NO_PARENT_SAMPLE_RATE
    );
  }
}
