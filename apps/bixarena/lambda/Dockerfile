# AWS provides the following base images for Python:
# https://docs.aws.amazon.com/lambda/latest/dg/python-image.html#python-image-base
FROM ghcr.io/astral-sh/uv:0.5.14 AS uv

FROM public.ecr.aws/lambda/python:3.13 AS builder

COPY --from=uv /uv /bin/uv

WORKDIR /build

# Install the pre-built shared library and its dependencies into /deps.
# boto3 is pre-installed in the base image.
COPY dist/*.tar.gz /tmp/
RUN uv pip install /tmp/*.tar.gz --target /deps --system --no-cache-dir

# ---- final image ----
FROM public.ecr.aws/lambda/python:3.13

# Add installed packages to the module search path
ENV PYTHONPATH="${LAMBDA_TASK_ROOT}:/deps"

COPY --from=builder /deps /deps

# Copy all handler modules
COPY leaderboard_snapshot/ ${LAMBDA_TASK_ROOT}/leaderboard_snapshot/

# Default handler (overridden per Lambda function in CDK)
CMD ["leaderboard_snapshot.handler.lambda_handler"]
