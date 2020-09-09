from sqlalchemy import and_, orm
from api import db
from api.db_models import Gene, TherapyType
from .general_resolvers import build_option_args, get_selection_set


def build_therapy_type_core_request(selection_set, name=None):
    """
    Builds a SQL request with just core therapy type fields.
    """
    sess = db.session

    therapy_type_1 = orm.aliased(TherapyType, name='p')

    core_field_mapping = {'name': therapy_type_1.name.label('name')}

    core = build_option_args(selection_set, core_field_mapping)

    query = sess.query(*core)

    if name:
        query = query.filter(therapy_type_1.name.in_(name))

    return query


def build_therapy_type_request(_obj, info, name=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)

    gene_1 = orm.aliased(Gene, name='g')
    therapy_type_1 = orm.aliased(TherapyType, name='pw')

    related_field_mapping = {'genes': 'genes'}

    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []

    query = sess.query(therapy_type_1)

    if name:
        query = query.filter(therapy_type_1.name.in_(name))

    if 'genes' in relations:
        query = query.join((gene_1, therapy_type_1.genes), isouter=True)
        option_args.append(orm.contains_eager(
            therapy_type_1.genes.of_type(gene_1)))

    if option_args:
        return query.options(*option_args)

    return build_therapy_type_core_request(selection_set, name)


def request_therapy_types(_obj, info, name=None):
    query = build_therapy_type_request(_obj, info, name=name)
    return query.distinct().all()
