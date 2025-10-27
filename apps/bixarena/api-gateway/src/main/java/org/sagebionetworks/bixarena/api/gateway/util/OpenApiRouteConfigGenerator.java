package org.sagebionetworks.bixarena.api.gateway.util;

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
 * Reads one or more OpenAPI YAML files and generates a canonical routes YAML for BixArena:
 *
 * bixarena:
 *   routes:
 *     - method: GET
 *       path: /api/foo
 *       audience: "urn:bixarena:api"
 *       anonymousAccess: false
 *
 * Usage:
 *   java ... OpenApiRouteConfigGenerator [--app-key=bixarena] [--out=src/main/resources/routes.yml] <openapi1.yaml> [openapi2.yaml ...]
 */
public final class OpenApiRouteConfigGenerator {

  private OpenApiRouteConfigGenerator() {}

  // ----- Config -----

  private static final Set<String> VALID_HTTP_METHODS = Set.of(
    "GET",
    "POST",
    "PUT",
    "DELETE",
    "PATCH",
    "HEAD",
    "OPTIONS",
    "TRACE"
  );

  /** The security scheme name in OpenAPI "security" section whose scopes we extract. */
  private static final String DEFAULT_SECURITY_SCHEME = "jwtBearer";

  /** Path prefix to prepend to each OpenAPI path (if your gateway mounts services at a versioned prefix). */
  private static final String DEFAULT_API_PREFIX = "";

  /** Map audience to path prefix. */
  private static final Map<String, String> AUDIENCE_PREFIX_MAP = Map.ofEntries(
    Map.entry("urn:bixarena:ai", ""),
    Map.entry("urn:bixarena:api", "/api/v1"),
    Map.entry("urn:bixarena:auth", "")
  );

  /** Default output location for the generated YAML. */
  private static final String DEFAULT_OUTPUT_PATH = "src/main/resources/routes.yml";

  /** Default top-level key under which routes are written. */
  private static final String DEFAULT_APP_KEY = "app";

  private static final String OAUTH2_AUDIENCE_EXTENSION = "x-oauth2-audience";
  private static final String ANONYMOUS_ACCESS_EXTENSION = "x-anonymous-access";

  private static final ObjectMapper YAML = new ObjectMapper(
    new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
  ); // no '---' header

  // ----- CLI -----

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println(
        "Usage: OpenApiRouteConfigGenerator [--app-key=bixarena] [--out=src/main/resources/routes.yml] <openapi1.yaml> [openapi2.yaml ...]"
      );
      System.exit(1);
    }

    String appKey = DEFAULT_APP_KEY;
    String outputPath = DEFAULT_OUTPUT_PATH;
    List<String> openapiFiles = new ArrayList<>();

    for (String a : args) {
      if (a.startsWith("--app-key=")) {
        appKey = a.substring("--app-key=".length()).trim();
      } else if (a.startsWith("--out=")) {
        outputPath = a.substring("--out=".length()).trim();
      } else {
        openapiFiles.add(a);
      }
    }

    if (openapiFiles.isEmpty()) {
      System.err.println("Error: no OpenAPI files provided.");
      System.exit(2);
    }

    try {
      List<RouteSpec> merged = new ArrayList<>();
      Set<RouteKey> seen = new HashSet<>();

      for (String file : openapiFiles) {
        System.err.println("Processing: " + file);
        List<RouteSpec> entries = parseOpenApiFile(file);
        for (RouteSpec e : entries) {
          RouteKey key = new RouteKey(e.method(), e.path());
          if (seen.add(key)) {
            merged.add(e);
          }
        }
      }

      // Stable sort by method then path for deterministic diffs
      merged.sort(Comparator.comparing((RouteSpec r) -> r.method()).thenComparing(r -> r.path()));

      writeYamlFile(appKey, merged, outputPath);
      System.err.println("Route configurations written to: " + outputPath);
    } catch (Exception e) {
      System.err.println("Error processing OpenAPI files: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  // ----- Parsing -----

  private static List<RouteSpec> parseOpenApiFile(String filePath) throws IOException {
    Path path = Paths.get(filePath);
    if (!Files.exists(path)) {
      throw new IOException("File not found: " + filePath);
    }
    if (!Files.isReadable(path)) {
      throw new IOException("File not readable: " + filePath);
    }

    JsonNode root = YAML.readTree(path.toFile());
    validateOpenApiStructure(root, filePath);

    String globalAudience = extractAudience(root);

    List<RouteSpec> out = new ArrayList<>();
    JsonNode paths = root.get("paths");
    if (paths == null || !paths.isObject()) {
      System.err.println("Warning: No 'paths' section in " + filePath);
      return out;
    }

    // Iterate path → method
    paths
      .fields()
      .forEachRemaining(pathEntry -> {
        String rawPath = pathEntry.getKey();
        JsonNode pathObj = pathEntry.getValue();

        pathObj
          .fields()
          .forEachRemaining(methodEntry -> {
            String method = methodEntry.getKey().toUpperCase(Locale.ROOT);
            JsonNode operation = methodEntry.getValue();

            if (!VALID_HTTP_METHODS.contains(method)) {
              return; // skip non-method keys like "parameters"
            }

            boolean anonymousAccess = extractAnonymous(operation);
            String audience = extractAudience(operation);
            if (audience == null) audience = globalAudience;

            // Fix: avoid passing null as key to getOrDefault
            String prefix = AUDIENCE_PREFIX_MAP.getOrDefault(
              audience != null ? audience : "",
              DEFAULT_API_PREFIX
            );

            // We include a route if any of the fields matter to the gateway.
            if (audience != null || anonymousAccess) {
              String normalizedPath = normalizePath(prefix + rawPath);
              RouteSpec entry = new RouteSpec(method, normalizedPath, audience, anonymousAccess);
              out.add(entry);
            }
          });
      });

    return out;
  }

  private static void validateOpenApiStructure(JsonNode root, String filePath) throws IOException {
    if (root == null || !root.isObject()) {
      throw new IOException("Invalid OpenAPI structure in: " + filePath);
    }
  }

  private static boolean extractAnonymous(JsonNode operation) {
    JsonNode n = operation.get(ANONYMOUS_ACCESS_EXTENSION);
    return n != null && n.isBoolean() && n.asBoolean();
  }

  private static String extractAudience(JsonNode node) {
    JsonNode n = node.get(OAUTH2_AUDIENCE_EXTENSION);
    if (n != null && n.isTextual()) {
      String a = n.asText().trim();
      return a.isEmpty() ? null : a;
    }
    return null;
  }

  private static String normalizePath(String raw) {
    String p = raw.trim().replaceAll("/{2,}", "/");
    if (!p.startsWith("/")) p = "/" + p;
    if (p.length() > 1 && p.endsWith("/")) p = p.substring(0, p.length() - 1);
    return p;
  }

  // ----- Output -----

  private static void writeYamlFile(String appKey, List<RouteSpec> routes, String outputPath)
    throws IOException {
    Path out = Paths.get(outputPath);
    Files.createDirectories(out.getParent());

    // Build YAML structure: appKey -> routes: [ { ... }, ... ]
    Map<String, Object> root = new LinkedHashMap<>();
    Map<String, Object> app = new LinkedHashMap<>();
    List<Map<String, Object>> list = new ArrayList<>();

    for (RouteSpec r : routes) {
      Map<String, Object> item = new LinkedHashMap<>();
      item.put("method", r.method());
      item.put("path", r.path());
      if (r.audience() != null) item.put("audience", r.audience());
      if (r.anonymousAccess()) item.put("anonymousAccess", true);
      list.add(item);
    }

    app.put("routes", list);
    root.put(appKey, app);

    YAML.writeValue(out.toFile(), root);
  }

  // ----- RouteSpec -----

  public static record RouteSpec(
    String method,
    String path,
    String audience,
    boolean anonymousAccess
  ) {}

  // ----- RouteKey -----

  private static record RouteKey(String method, String path) {}
}
