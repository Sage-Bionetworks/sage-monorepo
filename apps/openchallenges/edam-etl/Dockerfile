FROM python:3.12.2

COPY requirements.txt requirements.txt
COPY src/ /src

RUN pip3 install -r requirements.txt

WORKDIR /
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

# Set up the iAtlas database
CMD python /src/main.py