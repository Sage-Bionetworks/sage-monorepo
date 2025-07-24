# Base image containing the app built for the JVM.
FROM ghcr.io/sage-bionetworks/openchallenges-mcp-server-base:local

# Alternative: Build a statically linked native binary of the app using GraalVM.
# FROM mirror.gcr.io/busybox:1.36.1 AS busybox

# FROM scratch
# COPY --from=busybox /tmp /tmp
# COPY build/native/nativeCompile/openchallenges-mcp-server /openchallenges-mcp-server
# ENTRYPOINT ["/openchallenges-mcp-server"]