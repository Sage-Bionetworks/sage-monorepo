from sqlalchemy import and_, orm
from api import db
from api.db_models import Gene, SuperCategory
from .general_resolvers import build_option_args, get_selection_set


def build_super_category_core_request(selection_set, name=None):
    """
    Builds a SQL request with just core therapy type fields.
    """
    sess = db.session

    super_category_1 = orm.aliased(SuperCategory, name='p')

    core_field_mapping = {'name': super_category_1.name.label('name')}

    core = build_option_args(selection_set, core_field_mapping)

    query = sess.query(*core)

    if name:
        query = query.filter(super_category_1.name.in_(name))

    return query


def build_super_category_request(_obj, info, name=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(info=info)

    gene_1 = orm.aliased(Gene, name='g')
    super_category_1 = orm.aliased(SuperCategory, name='pw')

    related_field_mapping = {'genes': 'genes'}

    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []

    query = sess.query(super_category_1)

    if name:
        query = query.filter(super_category_1.name.in_(name))

    if 'genes' in relations:
        query = query.join((gene_1, super_category_1.genes), isouter=True)
        option_args.append(orm.contains_eager(
            super_category_1.genes.of_type(gene_1)))

    if option_args:
        return query.options(*option_args)

    return build_super_category_core_request(selection_set, name)


def request_super_categories(_obj, info, name=None):
    query = build_super_category_request(_obj, info, name=name)
    return query.distinct().all()
