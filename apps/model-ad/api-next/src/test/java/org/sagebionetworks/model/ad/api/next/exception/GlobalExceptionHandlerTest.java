package org.sagebionetworks.model.ad.api.next.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

class GlobalExceptionHandlerTest {

  private static final String REQUEST_METHOD = "GET";
  private static final String REQUEST_PATH = "/v1/comparison-tools/transcriptomics";

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
  private final Logger handlerLogger = (Logger) LoggerFactory.getLogger(
    GlobalExceptionHandler.class
  );
  private final ListAppender<ILoggingEvent> logAppender = new ListAppender<>();
  private ServletWebRequest request;

  @BeforeEach
  void setUp() {
    logAppender.start();
    handlerLogger.addAppender(logAppender);
    request = new ServletWebRequest(new MockHttpServletRequest(REQUEST_METHOD, REQUEST_PATH));
  }

  @AfterEach
  void tearDown() {
    handlerLogger.detachAppender(logAppender);
  }

  @Test
  @DisplayName("should log an unexpected exception at error with its method, path and stack trace")
  void shouldLogUnexpectedExceptionAtErrorWithMethodPathAndStackTrace() {
    RuntimeException unexpectedException = new IllegalStateException("database unavailable");

    handler.handleGenericException(unexpectedException, request, Locale.US);

    assertSingleErrorLogged(unexpectedException);
  }

  @Test
  @DisplayName("should return the generic internal server error body for an unexpected exception")
  void shouldReturnGenericInternalServerErrorBodyForUnexpectedException() {
    RuntimeException unexpectedException = new IllegalStateException("database unavailable");

    ResponseEntity<BasicErrorDto> response = handler.handleGenericException(
      unexpectedException,
      request,
      Locale.US
    );

    BasicErrorDto expectedBody = BasicErrorDto.builder()
      .title(ErrorConstants.INTERNAL_SERVER_ERROR.getTitle())
      .status(ErrorConstants.INTERNAL_SERVER_ERROR.getStatus().value())
      .detail(GlobalExceptionHandler.UNEXPECTED_ERROR_DETAIL)
      .instance(REQUEST_PATH)
      .build();
    assertThat(response.getStatusCode()).isEqualTo(
      ErrorConstants.INTERNAL_SERVER_ERROR.getStatus()
    );
    assertThat(response.getBody()).isEqualTo(expectedBody);
  }

  @Test
  @DisplayName("should log a data integrity error at error with its method, path and stack trace")
  void shouldLogDataIntegrityErrorAtErrorWithMethodPathAndStackTrace() {
    DataIntegrityException dataIntegrityException = new DataIntegrityException("sex", "unknown");

    handler.handleDataIntegrity(dataIntegrityException, request, Locale.US);

    assertSingleErrorLogged(dataIntegrityException);
  }

  @Test
  @DisplayName("should not log an expected client error")
  void shouldNotLogExpectedClientError() {
    handler.handleIllegalArgument(
      new IllegalArgumentException("invalid sort field"),
      request,
      Locale.US
    );

    assertThat(logAppender.list).isEmpty();
  }

  private void assertSingleErrorLogged(Throwable expectedException) {
    assertThat(logAppender.list).hasSize(1);
    ILoggingEvent event = logAppender.list.get(0);
    assertThat(event.getLevel()).isEqualTo(Level.ERROR);
    assertThat(event.getFormattedMessage()).contains(REQUEST_METHOD, REQUEST_PATH);
    assertThat(event.getThrowableProxy().getClassName()).isEqualTo(
      expectedException.getClass().getName()
    );
    assertThat(event.getThrowableProxy().getMessage()).isEqualTo(expectedException.getMessage());
  }
}
