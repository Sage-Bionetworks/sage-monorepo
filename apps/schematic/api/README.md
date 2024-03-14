# Run Schematic APIs 

## Running without Docker
To run the server, please execute the following from folder `apps/schematic/api`:
```
poetry shell
```
To install dependencies: 

```
poetry install 
```
And run schematic APIs: 

```
python3 -m schematic-api
```
and open your browser to here:

```
http://127.0.0.1:7080/api/v1/ui/
```

## Running with Docker

To run the server on a Docker container, please execute the following from the root directory:

```bash
# Prepare the development environment of the project with nx prepare schematic-api. This will create a venv and install all the Python dependencies.
nx prepare schematic-api

# You only need to run this command one time
# This step adds SSL private key and certificate as environment variable in .env file
python3 apps/schematic/api/prepare_key_certificate.py

# building the image
nx build-image schematic-api

# Start the containerized REST API with: 
nx serve-detach schematic-api
```
You could open your browser here: 
```
https://localhost:7443/api/v1/ui/
```
Note: When the OpenAPI description has changed, regenerate the REST API with nx run schematic-api:generate