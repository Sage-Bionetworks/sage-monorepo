# Base image containing the app built for the JVM.
FROM ghcr.io/sage-bionetworks/sage-monorepo-mcp-server-base:local

# Alternative: Build a statically linked native binary of the app using GraalVM.
# FROM busybox:1.36.1 AS busybox

# FROM scratch
# COPY --from=busybox /tmp /tmp
# COPY build/native/nativeCompile/sage-monorepo-mcp-server /sage-monorepo-mcp-server
# ENTRYPOINT ["/sage-monorepo-mcp-server"]