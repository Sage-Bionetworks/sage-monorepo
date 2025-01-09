FROM python:3.13.0-slim-bullseye AS builder

# Install the same version of Poetry as inside the dev container
RUN pip install --no-cache-dir poetry==1.8.3

WORKDIR /app
COPY pyproject.toml poetry.lock ./
RUN poetry export --without-hashes --format=requirements.txt > requirements.txt

FROM public.ecr.aws/lambda/python:3.13

COPY --from=builder /app/requirements.txt ${LAMBDA_TASK_ROOT}/
COPY openchallenges_data_lambda/app.py ${LAMBDA_TASK_ROOT}/

RUN python3.13 -m pip install --no-cache-dir -r requirements.txt -t .

# Command can be overwritten by providing a different command in the template directly.
CMD ["app.lambda_handler"]
