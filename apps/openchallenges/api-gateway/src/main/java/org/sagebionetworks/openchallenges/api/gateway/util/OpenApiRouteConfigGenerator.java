package org.sagebionetworks.openchallenges.api.gateway.util;

import static org.sagebionetworks.openchallenges.api.gateway.util.OpenApiConstants.ANONYMOUS_ACCESS_EXTENSION;
import static org.sagebionetworks.openchallenges.api.gateway.util.OpenApiConstants.OAUTH2_AUDIENCE_EXTENSION;

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
 * Reads one or more OpenAPI YAML files and generates a canonical route-config YAML:
 *
 * appKey:
 *   routes:
 *     - method: GET
 *       path: /api/v1/foo
 *       scopes: ["a:b"]
 *       audience: "urn:..."
 *       anonymousAccess: false
 *
 * Usage:
 *   java ... OpenApiRouteConfigGenerator [--app-key=app2] [--out=src/main/resources/route-config.yml] <openapi1.yml> [openapi2.yml ...]
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
  private static final String DEFAULT_API_PREFIX = "/api/v1";

  /** Default output location for the generated YAML. */
  private static final String DEFAULT_OUTPUT_PATH = "src/main/resources/routes.yml";

  /** Default top-level key under which routes are written. */
  private static final String DEFAULT_APP_KEY = "app";

  private static final ObjectMapper YAML = new ObjectMapper(
    new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
  ); // no '---' header

  // ----- CLI -----

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println(
        "Usage: OpenApiRouteConfigGenerator [--app-key=app2] [--out=src/main/resources/route-config.yml] <openapi1.yml> [openapi2.yml ...]"
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
      List<RouteEntry> merged = new ArrayList<>();
      Set<RouteKey> seen = new HashSet<>();

      for (String file : openapiFiles) {
        System.err.println("Processing: " + file);
        List<RouteEntry> entries = parseOpenApiFile(file);
        for (RouteEntry e : entries) {
          RouteKey key = new RouteKey(e.method, e.path);
          if (seen.add(key)) {
            merged.add(e);
          } else {
            // If a duplicate shows up across files, last-one-wins behavior could be implemented here.
            // For now, we keep the first and ignore duplicates to avoid accidental overrides.
          }
        }
      }

      // Stable sort by method then path for deterministic diffs
      merged.sort(Comparator.comparing((RouteEntry r) -> r.method).thenComparing(r -> r.path));

      writeYamlFile(appKey, merged, outputPath);
      System.err.println("Route configurations written to: " + outputPath);
    } catch (Exception e) {
      System.err.println("Error processing OpenAPI files: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  // ----- Parsing -----

  private static List<RouteEntry> parseOpenApiFile(String filePath) throws IOException {
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

    List<RouteEntry> out = new ArrayList<>();
    JsonNode paths = root.get("paths");
    if (paths == null || !paths.isObject()) {
      System.err.println("Warning: No 'paths' section in " + filePath);
      return out;
    }

    // Iterate path → method
    paths
      .properties()
      .forEach(pathEntry -> {
        String rawPath = pathEntry.getKey();
        JsonNode pathObj = pathEntry.getValue();

        pathObj
          .properties()
          .forEach(methodEntry -> {
            String method = methodEntry.getKey().toUpperCase(Locale.ROOT);
            JsonNode operation = methodEntry.getValue();

            if (!VALID_HTTP_METHODS.contains(method)) {
              return; // skip non-method keys like "parameters"
            }

            List<String> scopes = extractScopes(operation);
            boolean anonymousAccess = extractAnonymous(operation);

            // We include a route if any of the fields matter to the gateway.
            if (!scopes.isEmpty() || globalAudience != null || anonymousAccess) {
              String normalizedPath = normalizePath(DEFAULT_API_PREFIX + rawPath);
              RouteEntry entry = new RouteEntry(
                method,
                normalizedPath,
                new LinkedHashSet<>(scopes), // preserve order, dedupe
                globalAudience,
                anonymousAccess
              );
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
    // Optional: root.get("openapi") may be absent in some docs; not fatal for our extraction.
  }

  private static List<String> extractScopes(JsonNode operation) {
    List<String> scopes = new ArrayList<>();
    JsonNode security = operation.get("security");
    if (security == null || !security.isArray()) return scopes;

    for (JsonNode req : security) {
      JsonNode scheme = req.get(DEFAULT_SECURITY_SCHEME);
      if (scheme != null && scheme.isArray()) {
        for (JsonNode s : scheme) {
          if (s.isTextual()) scopes.add(s.asText());
        }
      }
    }
    return scopes;
  }

  private static boolean extractAnonymous(JsonNode operation) {
    JsonNode n = operation.get(ANONYMOUS_ACCESS_EXTENSION);
    return n != null && n.isBoolean() && n.asBoolean();
  }

  private static String extractAudience(JsonNode root) {
    JsonNode n = root.get(OAUTH2_AUDIENCE_EXTENSION);
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

  private static void writeYamlFile(String appKey, List<RouteEntry> routes, String outputPath)
    throws IOException {
    Path out = Paths.get(outputPath);
    Files.createDirectories(out.getParent());

    // Build YAML structure: appKey -> routes: [ { ... }, ... ]
    Map<String, Object> root = new LinkedHashMap<>();
    Map<String, Object> app = new LinkedHashMap<>();
    List<Map<String, Object>> list = new ArrayList<>();

    for (RouteEntry r : routes) {
      Map<String, Object> item = new LinkedHashMap<>();
      item.put("method", r.method);
      item.put("path", r.path);
      if (!r.scopes.isEmpty()) item.put("scopes", new ArrayList<>(r.scopes));
      if (r.audience != null) item.put("audience", r.audience);
      if (r.anonymousAccess) item.put("anonymousAccess", true);
      list.add(item);
    }

    app.put("routes", list);
    root.put(appKey, app);

    YAML.writeValue(out.toFile(), root);
  }

  // ----- Small types -----

  /** Internal route entry for generation (equals/hashCode by method+path). */
  private static final class RouteEntry {

    final String method;
    final String path;
    final LinkedHashSet<String> scopes;
    final String audience;
    final boolean anonymousAccess;

    RouteEntry(
      String method,
      String path,
      LinkedHashSet<String> scopes,
      String audience,
      boolean anonymousAccess
    ) {
      this.method = Objects.requireNonNull(method);
      this.path = Objects.requireNonNull(path);
      this.scopes = (scopes == null) ? new LinkedHashSet<>() : scopes;
      this.audience = (audience != null && !audience.isBlank()) ? audience : null;
      this.anonymousAccess = anonymousAccess;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof RouteEntry)) return false;
      RouteEntry that = (RouteEntry) o;
      return method.equals(that.method) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
      return Objects.hash(method, path);
    }
  }

  /** Key used only for deduping across files. */
  private static final class RouteKey {

    final String method;
    final String path;

    RouteKey(String method, String path) {
      this.method = method.toUpperCase(Locale.ROOT);
      this.path = path;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof RouteKey)) return false;
      RouteKey that = (RouteKey) o;
      return method.equals(that.method) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
      return Objects.hash(method, path);
    }
  }
}
