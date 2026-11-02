package org.sagebionetworks.explorers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import io.sentry.Sentry;
import io.sentry.exception.ExceptionMechanismException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

class UnhandledExceptionReporterTest {

  @Test
  @DisplayName("should report the exception to Sentry marked as unhandled")
  void shouldReportExceptionToSentryMarkedAsUnhandled() {
    RuntimeException unexpectedException = new IllegalStateException("database unavailable");
    ArgumentCaptor<Throwable> reported = ArgumentCaptor.forClass(Throwable.class);

    try (MockedStatic<Sentry> sentry = mockStatic(Sentry.class)) {
      UnhandledExceptionReporter.report(unexpectedException);

      sentry.verify(() -> Sentry.captureException(reported.capture()));
    }

    assertThat(reported.getValue()).isInstanceOf(ExceptionMechanismException.class);
    ExceptionMechanismException wrapper = (ExceptionMechanismException) reported.getValue();
    assertThat(wrapper.getThrowable()).isSameAs(unexpectedException);
    assertThat(wrapper.getExceptionMechanism().isHandled()).isFalse();
    assertThat(wrapper.getExceptionMechanism().getType()).isEqualTo(
      UnhandledExceptionReporter.MECHANISM_TYPE
    );
  }
}
