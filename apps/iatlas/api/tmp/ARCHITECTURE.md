# iAtlas API Architecture

In order to intuitively represent the [iAtlas data model](https://gitlab.com/cri-iatlas/iatlas-data/-/tree/staging/data_model) via an HTTPS API interface, the iAtlas API uses [GraphQL](https://graphql.org/). GraphQL is an easier-to-understand and more efficient interface for relational data than traditional REST conventions. It also has the advantage of being strongly typed and generating its own API schena for client applications to consume. An [interactive sandbox](https://iatlas-api/grapiql) for the iAtlas API is also available for experimenting with GraphQL queries and exploring documentation.

The API is implemented in Python 3 using [Flask](https://palletsprojects.com/p/flask/) and the [Ariadne](https://ariadnegraphql.org/) Python modules.

## Deployment Model

In the interests of simplifying dependency resolution and versioning, this repo will build Docker containers when staging and production releases are made. The best practice with Python HTTP(S)-based services is to use one or more instances of the API code running behind a reverse proxy using the uWSGI protocol.

This can be done local to one server using Docker and the `docker-compose` tool, or in a more orchestrated manner on a Kubernetes cluster with each K8S Pod consisting of one instance of the API container and another running Nginx as the reverse proxy.

## Database

The [`iatlas-data`](https://gitlab.com/cri-iatlas/iatlas-data) repo contains specifics about the database itself, which runs on top of [PostgreSQL](https://www.postgresql.org/).

Database configuration paremeters to the API should all be passed using environment variables.

## Telemetry

The API provides some telemetry data via the [prometheus-flask-exporter](https://pypi.org/project/prometheus-flask-exporter/) module. The key piece is the a distribution of query latency, which can and should be tied to telemetry at the DB layer to identify and address long-running queries.

## Logging

Logging is natively in JSON for consumption by a logging agent using the [Python JSON Logger](https://pypi.org/project/python-json-logger/) module.

## Docker Server Configuration

This was done on top of a newly launched Ubuntu 20.04 LTS deploy on an AWS `t3a.small` instance.

```bash
# Install pre-reqs
sudo apt-get -y update
sudo apt-get -y install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common
# Add the Docker GPG key
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
# Add the Docker repo
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
# Install the Docker engine
sudo apt-get -y update
sudo apt-get -y install docker-ce docker-ce-cli containerd.io docker-compose
# Add the Ubuntu user to the Docker group
sudo usermod -G -a docker ubuntu
```

In order for the Docker daemon to pull Sage-built images, it will need credentials on the image registry (e.g. the Gitlab image registry). On Gitlab, this is done by creating a Deploy Token, then taking the username and password values Gitlab presents and using them on the server like so:

```bash
docker login -u <username> -p <password> registry.gitlab.com
```

Docker will store and re-use those credentials after their first use.

Finally, in order to remotely instruct Docker to run new versions of the container when deployments are done, a secure SSH channel needs to be set up. This is done by creating a key pair and then providing the public key to the Docker instance and the private key to the Gitlab runner.
