package org.sagebionetworks.openchallenges.api.gateway.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Utility class to parse OpenAPI specifications and generate route configurations.
 * This tool reads OpenAPI YAML files and extracts security requirements and other
 * configuration properties for each endpoint, generating a YAML configuration
 * that can be used by the API Gateway.
 */
public class OpenApiRouteConfigGenerator {

  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  // Configuration constants
  private static final Set<String> VALID_HTTP_METHODS = Set.of(
    "GET",
    "POST",
    "PUT",
    "DELETE",
    "PATCH",
    "HEAD",
    "OPTIONS"
  );
  private static final String DEFAULT_SECURITY_SCHEME = "jwtBearer";
  private static final String ANONYMOUS_ACCESS_EXTENSION = "x-anonymous-access";
  private static final String DEFAULT_API_PREFIX = "/api/v1";
  private static final String DEFAULT_OUTPUT_PATH = "src/main/resources/route-config.yml";
  private static final String DEFAULT_CONFIG_KEY = "route-config";

  static {
    // Configure YAML mapper to not output document start marker (---)
    ((YAMLFactory) yamlMapper.getFactory()).configure(
        YAMLGenerator.Feature.WRITE_DOC_START_MARKER,
        false
      );
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("Usage: OpenApiRouteConfigGenerator <openapi-file1> [openapi-file2] ...");
      System.err.println("Example: OpenApiRouteConfigGenerator organization-service.openapi.yaml");
      System.exit(1);
    }

    try {
      Map<String, Map<String, Object>> routeConfigs = new LinkedHashMap<>();

      for (String filePath : args) {
        System.err.println("Processing: " + filePath);
        Map<String, Map<String, Object>> fileConfigs = parseOpenApiFile(filePath);
        routeConfigs.putAll(fileConfigs);
      }

      // Write the result to a YAML file in the resources folder
      String outputPath = DEFAULT_OUTPUT_PATH;
      writeYamlFile(routeConfigs, outputPath);
      System.err.println("Route configurations written to: " + outputPath);
    } catch (Exception e) {
      System.err.println("Error processing OpenAPI files: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Parse a single OpenAPI file and extract route configurations.
   */
  private static Map<String, Map<String, Object>> parseOpenApiFile(String filePath)
    throws IOException {
    Path path = Paths.get(filePath);
    if (!Files.exists(path)) {
      throw new IOException("File not found: " + filePath);
    }
    if (!Files.isReadable(path)) {
      throw new IOException("File not readable: " + filePath);
    }

    try {
      JsonNode root = yamlMapper.readTree(path.toFile());
      validateOpenApiStructure(root, filePath);
      return extractRouteConfigurations(root, filePath);
    } catch (Exception e) {
      throw new IOException("Failed to parse OpenAPI file: " + filePath, e);
    }
  }

  /**
   * Validate basic OpenAPI structure.
   */
  private static void validateOpenApiStructure(JsonNode root, String filePath) throws IOException {
    if (root == null || !root.isObject()) {
      throw new IOException("Invalid OpenAPI structure in: " + filePath);
    }

    JsonNode openapi = root.get("openapi");
    if (openapi == null) {
      System.err.println("Warning: No OpenAPI version found in " + filePath);
    }
  }

  /**
   * Extract route configurations from OpenAPI paths.
   */
  private static Map<String, Map<String, Object>> extractRouteConfigurations(
    JsonNode root,
    String filePath
  ) {
    Map<String, Map<String, Object>> routeConfigs = new LinkedHashMap<>();

    JsonNode paths = root.get("paths");
    if (paths == null) {
      System.err.println("Warning: No 'paths' section found in " + filePath);
      return routeConfigs;
    }

    // Iterate through each path
    paths
      .properties()
      .forEach(pathEntry -> {
        String path = pathEntry.getKey();
        JsonNode pathObject = pathEntry.getValue();

        // Iterate through each HTTP method for this path
        pathObject
          .properties()
          .forEach(methodEntry -> {
            String method = methodEntry.getKey().toUpperCase();
            JsonNode operation = methodEntry.getValue();

            // Skip if this is not an HTTP method (e.g., parameters, $ref)
            if (!isHttpMethod(method)) {
              return;
            }

            // Extract security requirements for this operation
            List<String> scopes = extractScopes(operation);
            if (!scopes.isEmpty()) {
              // Prepend /api/v1 to match the gateway's incoming request paths
              String routeKey = method + " " + DEFAULT_API_PREFIX + path;
              Map<String, Object> routeConfig = routeConfigs.computeIfAbsent(routeKey, k ->
                new LinkedHashMap<>()
              );
              routeConfig.put("scopes", scopes);

              // Extract anonymous access flag
              JsonNode anonymousAccess = operation.get(ANONYMOUS_ACCESS_EXTENSION);
              if (anonymousAccess != null && anonymousAccess.isBoolean()) {
                routeConfig.put("anonymousAccess", anonymousAccess.asBoolean());
              }
            }
          });
      });

    return routeConfigs;
  }

  /**
   * Check if the given string is a valid HTTP method.
   */
  private static boolean isHttpMethod(String method) {
    return VALID_HTTP_METHODS.contains(method);
  }

  /**
   * Extract scopes from an OpenAPI operation's security requirements.
   */
  private static List<String> extractScopes(JsonNode operation) {
    List<String> scopes = new ArrayList<>();

    JsonNode security = operation.get("security");
    if (security == null || !security.isArray()) {
      return scopes;
    }

    // Iterate through security requirements
    for (JsonNode securityRequirement : security) {
      // Look for jwtBearer security scheme
      JsonNode jwtBearer = securityRequirement.get(DEFAULT_SECURITY_SCHEME);
      if (jwtBearer != null && jwtBearer.isArray()) {
        for (JsonNode scopeNode : jwtBearer) {
          if (scopeNode.isTextual()) {
            scopes.add(scopeNode.asText());
          }
        }
      }
    }

    return scopes;
  }

  /**
   * Write the route configurations to a YAML file.
   */
  private static void writeYamlFile(
    Map<String, Map<String, Object>> routeConfigs,
    String outputPath
  ) throws IOException {
    // Create the directory if it doesn't exist
    Path filePath = Paths.get(outputPath);
    Files.createDirectories(filePath.getParent());

    // Create the YAML structure for Spring Boot configuration
    Map<String, Object> config = new LinkedHashMap<>();
    Map<String, Object> app = new LinkedHashMap<>();

    if (routeConfigs.isEmpty()) {
      app.put(DEFAULT_CONFIG_KEY, new LinkedHashMap<>());
    } else {
      app.put(DEFAULT_CONFIG_KEY, routeConfigs);
    }

    config.put("app", app);

    // Write to file
    yamlMapper.writeValue(filePath.toFile(), config);

    // Also print to console for immediate reference
    System.out.println("# Generated route configurations for API Gateway");
    System.out.println("# File written to: " + outputPath);
    System.out.println();
    if (routeConfigs.isEmpty()) {
      System.out.println("No routes with security requirements found");
    } else {
      System.out.println("Routes with security requirements:");
      for (Map.Entry<String, Map<String, Object>> entry : routeConfigs.entrySet()) {
        String route = entry.getKey();
        @SuppressWarnings("unchecked")
        List<String> scopes = (List<String>) entry.getValue().get("scopes");
        Boolean anonymousAccess = (Boolean) entry.getValue().get("anonymousAccess");
        System.out.println(
          "  " +
          route +
          " -> scopes: " +
          scopes +
          (anonymousAccess != null && anonymousAccess ? " [anonymous]" : "")
        );
      }
    }
  }
}
