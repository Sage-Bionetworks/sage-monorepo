from flask import Blueprint
from ariadne import make_executable_schema
from .helloType import hello_query
from flaskr.resolvers import resolvers

bp = Blueprint('schema', __name__)

type_defs = [hello_query]

schema = make_executable_schema(type_defs, resolvers)
