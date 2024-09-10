package org.sagebionetworks.challenge.exception;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(
    HttpServletRequest httpServletRequest,
    HttpServletResponse httpServletResponse,
    AccessDeniedException e
  ) throws IOException, ServletException {
    httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
  }
}
