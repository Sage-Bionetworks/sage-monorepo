from sqlalchemy import and_, orm
from api import db
from api.db_models import MutationType
from .general_resolvers import get_selected, get_value

mutation_type_request_fields = {'display', 'name'}


def build_mutation_type_graphql_response(mutation_type):
    if not mutation_type:
        return None
    return {
        'display': get_value(mutation_type, 'display'),
        'name': get_value(mutation_type)
    }


def build_mutation_type_request(requested):
    """
    Builds a SQL request.
    """
    sess = db.session

    mutation_type_1 = orm.aliased(MutationType, name='mt')

    core_field_mapping = {'display': mutation_type_1.display.label('display'),
                          'name': mutation_type_1.name.label('name')}
    core = get_selected(requested, core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(mutation_type_1)

    order = []
    append_to_order = order.append
    if 'name' in requested:
        append_to_order(mutation_type_1.name)
    if 'display' in requested:
        append_to_order(mutation_type_1.display)

    query = query.order_by(*order) if order else query

    return query


def request_mutation_types(requested):
    query = build_mutation_type_request(requested)
    return query.all()
