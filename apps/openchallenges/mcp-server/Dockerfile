# Build in a container with Oracle GraalVM Native Image and MUSL
FROM container-registry.oracle.com/graalvm/native-image:21-muslib AS nativebuild
WORKDIR /build

# hadolint ignore=DL3040,DL3041
RUN microdnf -y install wget xz unzip findutils

# Install Gradle
ARG GRADLE_VERSION=8.14
ARG GRADLE_ARCHIVE=gradle-${GRADLE_VERSION}-bin.zip
RUN wget -q https://services.gradle.org/distributions/${GRADLE_ARCHIVE} && \
    unzip ${GRADLE_ARCHIVE} && \
    rm -f ${GRADLE_ARCHIVE}

ENV PATH="/build/gradle-${GRADLE_VERSION}/bin:$PATH"

# Build the native image
COPY gradle gradle
COPY build.gradle* gradle* settings.gradle* ./
COPY src src
RUN gradle clean nativeCompile --no-daemon

FROM busybox:1.36.1 AS busybox

# Prepare the final image
FROM scratch
COPY --from=busybox /tmp /tmp
COPY --from=nativebuild /build/build/native/nativeCompile/openchallenges-mcp-server /openchallenges-mcp-server
ENTRYPOINT ["/openchallenges-mcp-server"]