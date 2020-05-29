from .main import bp
from .schema import schema
from ariadne import graphql_sync
from ariadne.constants import PLAYGROUND_HTML
from flask import current_app, jsonify, request


@bp.route("/graphiql", methods=["GET"])
def graphql_playgroud():
    # On GET request serve GraphQL Playground
    # You don't need to provide Playground if you don't want to
    # but keep on mind this will not prohibit clients from
    # exploring your API using desktop GraphQL Playground app.
    return PLAYGROUND_HTML, 200


@bp.route("/graphiql", methods=["POST"])
@bp.route("/api", methods=["POST"])
def graphql_server():
    # GraphQL queries are always sent as POST
    data = request.get_json()

    # Note: Passing the request to the context is optional.
    # In Flask, the current request is always accessible as flask.request
    success, result = graphql_sync(
        schema,
        data,
        context_value=request,
        debug=current_app.debug
    )

    status_code = 200 if success else 400
    return jsonify(result), status_code


@bp.route("/home")
def home():
    return "I'm home!"
