from flask import Blueprint
from .helloResolver import hello

bp = Blueprint('resolvers', __name__)

resolvers = [hello]
