#!/usr/bin/env python

from flask import Flask
from flask_graphql import GraphQLView
from app.database import db_session, init_db
from app.schema import schema
from os import getenv

app = Flask(__name__)
app.debug = True

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
    '/graphiql',
    view_func=GraphQLView.as_view(
        'graphqil', schema=schema, graphiql=True
    )
)

# Adding batch query support (used in Apollo-Client)
app.add_url_rule(
    '/graphiql-batch',
    view_func=GraphQLView.as_view(
        'graphiql-batch', schema=schema, batch=True
    )
)


@app.teardown_appcontext
def shutdown_session(exception=None):
    db_session.remove()


if __name__ == '__main__':
    init_db()
    app.run(host='0.0.0.0', port=getenv('PORT'))
