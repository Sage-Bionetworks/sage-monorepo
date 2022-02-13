#!/usr/bin/env python3

import connexion
import flask
from flask_cors import CORS
from mongoengine import connect

from openapi_server import encoder
from openapi_server.config import config

app = connexion.App(__name__, specification_dir="./openapi/")
app.app.json_encoder = encoder.JSONEncoder
app.add_api("openapi.yaml", pythonic_params=True)

app.add_url_rule("/ui", "ui", lambda: flask.redirect("/api/v1/ui"))

# add CORS support
# https://connexion.readthedocs.io/en/latest/cookbook.html#cors-support
CORS(app.app, resources={r"/api/*": {"origins": "*"}})

print(f"Server secret key: {config.secret_key}")

connect(
    db=config.db_database,
    username=config.db_username,
    password=config.db_password,
    host=config.db_host,
)


def main():
    app.run(port=8080, debug=False)


if __name__ == "__main__":
    main()
