package org.sagebionetworks.explorers;

import io.sentry.Sentry;
import io.sentry.exception.ExceptionMechanismException;
import io.sentry.protocol.Mechanism;

/**
 * Reports an exception that the API answers with a server error to Sentry, marked unhandled since
 * no controller recovered from it, so that Sentry counts it as a crash.
 */
public final class UnhandledExceptionReporter {

  static final String MECHANISM_TYPE = "GlobalExceptionHandler";

  private UnhandledExceptionReporter() {}

  public static void report(Throwable throwable) {
    Mechanism mechanism = new Mechanism();
    mechanism.setType(MECHANISM_TYPE);
    mechanism.setHandled(false);
    Sentry.captureException(
      new ExceptionMechanismException(mechanism, throwable, Thread.currentThread())
    );
  }
}
