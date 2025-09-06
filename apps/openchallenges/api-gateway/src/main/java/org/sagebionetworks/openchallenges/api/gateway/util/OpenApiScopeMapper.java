package org.sagebionetworks.openchallenges.api.gateway.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Utility class to parse OpenAPI specifications and generate route-to-scope mappings.
 * This tool reads OpenAPI YAML files and extracts security requirements for each endpoint,
 * generating a YAML configuration that can be used by the API Gateway.
 */
public class OpenApiScopeMapper {

  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("Usage: OpenApiScopeMapper <openapi-file1> [openapi-file2] ...");
      System.err.println("Example: OpenApiScopeMapper organization-service.openapi.yaml");
      System.exit(1);
    }

    try {
      Map<String, Map<String, List<String>>> routeScopes = new LinkedHashMap<>();
      
      for (String filePath : args) {
        System.err.println("Processing: " + filePath);
        Map<String, Map<String, List<String>>> fileScopes = parseOpenApiFile(filePath);
        routeScopes.putAll(fileScopes);
      }

      // Output the result in YAML format
      generateYamlOutput(routeScopes);

    } catch (Exception e) {
      System.err.println("Error processing OpenAPI files: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Parse a single OpenAPI file and extract route-to-scope mappings.
   */
  private static Map<String, Map<String, List<String>>> parseOpenApiFile(String filePath) throws IOException {
    File file = new File(filePath);
    if (!file.exists()) {
      throw new IOException("File not found: " + filePath);
    }

    JsonNode root = yamlMapper.readTree(file);
    Map<String, Map<String, List<String>>> routeScopes = new LinkedHashMap<>();

    JsonNode paths = root.get("paths");
    if (paths == null) {
      System.err.println("Warning: No 'paths' section found in " + filePath);
      return routeScopes;
    }

    // Iterate through each path
    paths.fields().forEachRemaining(pathEntry -> {
      String path = pathEntry.getKey();
      JsonNode pathObject = pathEntry.getValue();

      // Iterate through each HTTP method for this path
      pathObject.fields().forEachRemaining(methodEntry -> {
        String method = methodEntry.getKey().toUpperCase();
        JsonNode operation = methodEntry.getValue();

        // Skip if this is not an HTTP method (e.g., parameters, $ref)
        if (!isHttpMethod(method)) {
          return;
        }

        // Extract security requirements for this operation
        List<String> scopes = extractScopes(operation);
        if (!scopes.isEmpty()) {
          String routeKey = method + " " + path;
          routeScopes.computeIfAbsent(routeKey, k -> new LinkedHashMap<>())
              .put("scopes", scopes);
        }
      });
    });

    return routeScopes;
  }

  /**
   * Check if the given string is a valid HTTP method.
   */
  private static boolean isHttpMethod(String method) {
    return Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS")
        .contains(method);
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
      JsonNode jwtBearer = securityRequirement.get("jwtBearer");
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
   * Generate YAML output for the route-to-scope mapping.
   */
  private static void generateYamlOutput(Map<String, Map<String, List<String>>> routeScopes) {
    System.out.println("# Generated route-to-scope mapping for API Gateway");
    System.out.println("# Copy this configuration to your application.yml file");
    System.out.println();
    System.out.println("openchallenges:");
    System.out.println("  gateway:");
    System.out.println("    route-scopes:");
    
    if (routeScopes.isEmpty()) {
      System.out.println("      {} # No routes with security requirements found");
      return;
    }

    for (Map.Entry<String, Map<String, List<String>>> entry : routeScopes.entrySet()) {
      String route = entry.getKey();
      Map<String, List<String>> routeConfig = entry.getValue();
      List<String> scopes = routeConfig.get("scopes");
      
      System.out.println("      \"" + route + "\":");
      System.out.print("        scopes: [");
      System.out.print(String.join(", ", scopes));
      System.out.println("]");
    }
  }
}
