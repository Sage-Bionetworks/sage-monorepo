# Start with a bare Alpine Linux to keep the container image small
FROM tiangolo/uwsgi-nginx-flask:python3.8

ENV APP_DIR=/app

# WORKDIR /app
# COPY . /app

# Install the PyPI dependencies using pip
# RUN pip3 install --no-cache-dir -r requirements.txt

WORKDIR ${APP_DIR}
COPY . /app

# Use the version of Poetry installed in the dev container.
# See /workspaces/sage-monorepo/tools/devcontainers/sage/.devcontainer/Dockerfile
# RUN pip install --no-cache-dir poetry==1.6.1 \
#   && poetry config --local virtualenvs.create false \
#   && poetry install --with prod --no-root --no-interaction --no-ansi \
#   && pip cache purge \
#   # Give APP_USER write permission to directory where synapse data will be written
#   && mkdir -p ${LOCAL_DATA_DIR} \
#   && chown ${APP_USERNAME} ${LOCAL_DATA_DIR}
