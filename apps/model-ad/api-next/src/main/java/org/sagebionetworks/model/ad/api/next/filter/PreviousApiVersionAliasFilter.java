package org.sagebionetworks.model.ad.api.next.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Serves requests for the previous API version's paths from the current version's controllers.
 *
 * During a release, the deployed proxy is replaced after this API, so for the length of that
 * rollout the previous proxy keeps sending previous-version paths to this API. Once the new proxy
 * is live it answers previous-version paths itself, so none reach this filter.
 *
 * The path is rewritten without decoding it, so encoded slashes in path variables (such as model
 * names like "5xFAD (IU/Jax/Pitt)") reach the controllers unchanged.
 */
// TODO: remove in the release after v1.1.0 (EX-214)
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PreviousApiVersionAliasFilter extends OncePerRequestFilter {

  static final String PREVIOUS_BASE_PATH = "/v1";

  private final String currentBasePath;

  public PreviousApiVersionAliasFilter(
    @Value("${openapi.modelADAPINext.base-path:/v2}") String currentBasePath
  ) {
    this.currentBasePath = currentBasePath;
  }

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    String pathWithinApplication = request
      .getRequestURI()
      .substring(request.getContextPath().length());
    if (isPreviousVersionPath(pathWithinApplication)) {
      filterChain.doFilter(new CurrentVersionRequest(request), response);
    } else {
      filterChain.doFilter(request, response);
    }
  }

  private static boolean isPreviousVersionPath(String path) {
    return path.startsWith(PREVIOUS_BASE_PATH + "/");
  }

  private String toCurrentVersionPath(String path) {
    return isPreviousVersionPath(path)
      ? currentBasePath + path.substring(PREVIOUS_BASE_PATH.length())
      : path;
  }

  private final class CurrentVersionRequest extends HttpServletRequestWrapper {

    CurrentVersionRequest(HttpServletRequest request) {
      super(request);
    }

    @Override
    public String getRequestURI() {
      String contextPath = getContextPath();
      return (
        contextPath + toCurrentVersionPath(super.getRequestURI().substring(contextPath.length()))
      );
    }

    @Override
    public StringBuffer getRequestURL() {
      StringBuffer previousUrl = super.getRequestURL();
      String previousUri = super.getRequestURI();
      int pathStart = previousUrl.length() - previousUri.length();
      return new StringBuffer(previousUrl.substring(0, pathStart)).append(getRequestURI());
    }

    @Override
    public String getServletPath() {
      return toCurrentVersionPath(super.getServletPath());
    }
  }
}
