# Start with a bare Alpine Linux to keep the container image small
FROM tiangolo/uwsgi-nginx-flask:python3.8

WORKDIR /app
COPY . /app

# Install the PyPI dependencies using pip
RUN pip3 install --no-cache-dir -r requirements.txt
