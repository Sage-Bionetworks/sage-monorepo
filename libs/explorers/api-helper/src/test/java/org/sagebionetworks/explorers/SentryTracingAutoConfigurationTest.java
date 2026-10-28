package org.sagebionetworks.explorers;

import static org.assertj.core.api.Assertions.assertThat;

import io.sentry.SentryOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class SentryTracingAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
    .withConfiguration(AutoConfigurations.of(SentryTracingAutoConfiguration.class));

  @Test
  @DisplayName("should register the parent sampled sampler when a traces sample rate is set")
  void shouldRegisterTheParentSampledSamplerWhenATracesSampleRateIsSet() {
    contextRunner
      .withPropertyValues("sentry.traces-sample-rate=1.0")
      .run(context ->
        assertThat(context)
          .getBean(SentryOptions.TracesSamplerCallback.class)
          .isInstanceOf(ParentSampledTracesSampler.class)
      );
  }

  @Test
  @DisplayName("should not register a sampler when the traces sample rate is empty")
  void shouldNotRegisterASamplerWhenTheTracesSampleRateIsEmpty() {
    contextRunner
      .withPropertyValues("sentry.traces-sample-rate=")
      .run(context -> assertThat(context).doesNotHaveBean(SentryOptions.TracesSamplerCallback.class)
      );
  }

  @Test
  @DisplayName("should not register a sampler when the traces sample rate is unset")
  void shouldNotRegisterASamplerWhenTheTracesSampleRateIsUnset() {
    contextRunner.run(context ->
      assertThat(context).doesNotHaveBean(SentryOptions.TracesSamplerCallback.class)
    );
  }
}
