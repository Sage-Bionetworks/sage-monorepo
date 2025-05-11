FROM busybox:1.36.1 AS busybox

FROM scratch
COPY --from=busybox /tmp /tmp
COPY build/native/nativeCompile/openchallenges-mcp-server /openchallenges-mcp-server
ENTRYPOINT ["/openchallenges-mcp-server"]