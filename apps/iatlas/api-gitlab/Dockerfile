# Start with a bare Alpine Linux to keep the container image small
FROM alpine:3.11

# uWSGI will listen on port 8080
EXPOSE 3031 9040

# Use '/app' as the application root
WORKDIR /app

# Install Python3 and uWSGI
RUN apk add --no-cache \
    uwsgi-python3 \
    python3

# Copy the project directory into the container
# Use the .dockeringore file to exclude items
COPY . .

# Install the PyPI dependencies using pip
RUN pip3 install --no-cache-dir -r requirements.txt

# The uWSGI process will serve the Python code
CMD ["/usr/sbin/uwsgi", "--http-socket", "0.0.0.0:3031", \
    "--uid", "uwsgi", \
    "--plugins", "python3", \
    "--stats" , "127.0.0.1:9040", "--stats-http",  \
    "--processes", "1", "--threads", "4", \
    "-w", "app:app" ]