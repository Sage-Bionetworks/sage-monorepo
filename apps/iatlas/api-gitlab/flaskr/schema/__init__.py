from ariadne import make_executable_schema
from .helloType import hello_query
from flaskr.resolvers import resolvers

type_defs = [hello_query]


schema = make_executable_schema(type_defs, resolvers)