#!/usr/bin/env python3
import connexion
from flask_cors import CORS
import flask
from schematic_api import encoder

app = connexion.App(__name__, specification_dir="./openapi/")
app.app.json_encoder = encoder.JSONEncoder
app.add_api(
    "openapi.yaml", arguments={"title": "Schematic REST API"}, pythonic_params=True
)
app.add_url_rule("/", "ui", lambda: flask.redirect("/api/v1/ui"))

# add CORS support
# https://connexion.readthedocs.io/en/latest/cookbook.html#cors-support
CORS(app.app, resources={r"/api/*": {"origins": "*"}})


def main():
    app.run(port=7443, debug=False)


if __name__ == "__main__":
    main()
