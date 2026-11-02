package org.sagebionetworks.explorers;

import io.sentry.SentryOptions;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;

/**
 * Registers {@link ParentSampledTracesSampler} with the Sentry Spring Boot starter when performance
 * tracing is on, that is when {@code sentry.traces-sample-rate} is set, by the
 * {@code SENTRY_TRACES_SAMPLE_RATE} environment variable. Any sampler turns tracing on, so the bean
 * must not exist when the rate is unset or empty; {@code @ConditionalOnProperty} would still match
 * an empty value.
 */
@AutoConfiguration
public class SentryTracingAutoConfiguration {

  @Bean
  @ConditionalOnExpression("'${sentry.traces-sample-rate:}' != ''")
  SentryOptions.TracesSamplerCallback parentSampledTracesSampler() {
    return new ParentSampledTracesSampler();
  }
}
