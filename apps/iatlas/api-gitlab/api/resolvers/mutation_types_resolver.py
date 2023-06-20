from sqlalchemy import orm
from api import db
from api.database import return_mutation_type_query
from api.db_models import MutationType
from .resolver_helpers import build_mutation_type_graphql_response, get_requested, get_selection_set, mutation_type_request_fields, request_mutation_types


def resolve_mutation_types(_obj, info):
    requested = get_requested(info, mutation_type_request_fields)

    return map(build_mutation_type_graphql_response, request_mutation_types(requested))
