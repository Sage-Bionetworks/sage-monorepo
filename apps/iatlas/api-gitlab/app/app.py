#!/usr/bin/env python

from flask import Flask

from database import db_session, init_db
from flask_graphql import GraphQLView
from schema import schema
from os import getenv

app = Flask(__name__)
app.debug = True

print("In here", flush=True)

example_query = """
{
  allEmployees(sort: [NAME_ASC, ID_ASC]) {
    edges {
      node {
        id
        name
        department {
          id
          name
        }
        role {
          id
          name
        }
      }
    }
  }
}
"""


app.add_url_rule(
    "/graphiql", view_func=GraphQLView.as_view("graphql", schema=schema, graphiql=True)
)


@app.teardown_appcontext
def shutdown_session(exception=None):
    db_session.remove()


if __name__ == "__main__":
    init_db()
    app.run(debug=True, host="0.0.0.0", port=getenv("PORT"))
