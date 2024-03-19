FROM python:3.12.0-slim

ENV APP_DIR=/opt/app

WORKDIR ${APP_DIR}
COPY src src/
COPY pyproject.toml poetry.lock ./

# Use the version of Poetry installed in the dev container.
# See /workspaces/sage-monorepo/tools/devcontainers/sage/.devcontainer/Dockerfile
RUN pip install --no-cache-dir poetry==1.6.1 \
  && poetry config --local virtualenvs.create false \
  && poetry install --with prod --no-root --no-interaction --no-ansi \
  && pip cache purge

WORKDIR /
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["python", "${APP_DIR}/src/main.py"]