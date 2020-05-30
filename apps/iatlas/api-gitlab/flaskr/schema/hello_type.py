from ariadne import load_schema_from_path
import os

dirname, _filename = os.path.split(os.path.abspath(__file__))

hello_query = load_schema_from_path(dirname + "/hello.query.graphql")
