package org.sagebionetworks.openchallenges.api.gateway.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
      Map<String, Map<String, Object>> routeScopes = new LinkedHashMap<>();
      
      for (String filePath : args) {
        System.err.println("Processing: " + filePath);
        Map<String, Map<String, Object>> fileScopes = parseOpenApiFile(filePath);
        routeScopes.putAll(fileScopes);
      }

      // Write the result to a YAML file in the resources folder
      String outputPath = "src/main/resources/route-scopes.yml";
      writeYamlFile(routeScopes, outputPath);
      System.err.println("Route-to-scope mappings written to: " + outputPath);

    } catch (Exception e) {
      System.err.println("Error processing OpenAPI files: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Parse a single OpenAPI file and extract route-to-scope mappings.
   */
  private static Map<String, Map<String, Object>> parseOpenApiFile(String filePath) throws IOException {
    File file = new File(filePath);
    if (!file.exists()) {
      throw new IOException("File not found: " + filePath);
    }

    JsonNode root = yamlMapper.readTree(file);
    Map<String, Map<String, Object>> routeScopes = new LinkedHashMap<>();

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
          // Prepend /api/v1 to match the gateway's incoming request paths
          String routeKey = method + " /api/v1" + path;
          Map<String, Object> routeConfig = routeScopes.computeIfAbsent(routeKey, k -> new LinkedHashMap<>());
          routeConfig.put("scopes", scopes);
          
          // Extract anonymous access flag
          JsonNode anonymousAccess = operation.get("x-anonymous-access");
          if (anonymousAccess != null && anonymousAccess.isBoolean()) {
            routeConfig.put("anonymousAccess", anonymousAccess.asBoolean());
          }
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
   * Write the route-to-scope mapping to a YAML file.
   */
  private static void writeYamlFile(Map<String, Map<String, Object>> routeScopes, String outputPath) throws IOException {
    // Create the directory if it doesn't exist
    Path filePath = Paths.get(outputPath);
    Files.createDirectories(filePath.getParent());
    
    // Create the YAML structure for Spring Boot configuration
    Map<String, Object> config = new LinkedHashMap<>();
    Map<String, Object> openchallenges = new LinkedHashMap<>();
    Map<String, Object> gateway = new LinkedHashMap<>();
    
    if (routeScopes.isEmpty()) {
      gateway.put("route-scopes", new LinkedHashMap<>());
    } else {
      gateway.put("route-scopes", routeScopes);
    }
    
    openchallenges.put("gateway", gateway);
    config.put("openchallenges", openchallenges);
    
    // Write to file
    yamlMapper.writeValue(filePath.toFile(), config);
    
    // Also print to console for immediate reference
    System.out.println("# Generated route-to-scope mapping for API Gateway");
    System.out.println("# File written to: " + outputPath);
    System.out.println();
    if (routeScopes.isEmpty()) {
      System.out.println("No routes with security requirements found");
    } else {
      System.out.println("Routes with security requirements:");
      for (Map.Entry<String, Map<String, Object>> entry : routeScopes.entrySet()) {
        String route = entry.getKey();
        @SuppressWarnings("unchecked")
        List<String> scopes = (List<String>) entry.getValue().get("scopes");
        Boolean anonymousAccess = (Boolean) entry.getValue().get("anonymousAccess");
        System.out.println("  " + route + " -> scopes: " + scopes + 
                          (anonymousAccess != null && anonymousAccess ? " [anonymous]" : ""));
      }
    }
  }
}
