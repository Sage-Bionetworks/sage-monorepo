package org.sagebionetworks.openchallenges.api.gateway.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;

/**
 * Raw spec bound from YAML.
 * This is converted to the canonical RouteConfig + RouteKey at startup.
 */
public record RouteSpec(
  @NotBlank
  @Pattern(
    regexp = "GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS|TRACE",
    message = "Unsupported HTTP method"
  )
  String method,

  @NotBlank @Size(max = 2048) String path,

  Set<String> scopes,

  String audience,

  boolean anonymousAccess
) {}
