from sqlalchemy import and_, orm
from api import db
from api.db_models import Feature, MethodTag
from .general_resolvers import build_option_args, get_selection_set


def build_method_tag_core_request(selection_set, name=None):
    """
    Builds a SQL request with just core method tag fields.
    """
    sess = db.session

    method_tag_1 = orm.aliased(MethodTag, name='mt')

    core_field_mapping = {'name': method_tag_1.name.label('name')}

    core = build_option_args(selection_set, core_field_mapping)

    query = sess.query(*core)

    if name:
        query = query.filter(method_tag_1.name.in_(name))

    return query


def build_method_tag_request(_obj, info, name=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(info=info)

    feature_1 = orm.aliased(Feature, name='f')
    method_tag_1 = orm.aliased(MethodTag, name='mt')

    related_field_mapping = {'features': 'features'}

    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []

    query = sess.query(method_tag_1)

    if name:
        query = query.filter(method_tag_1.name.in_(name))

    if 'features' in relations:
        query = query.join((feature_1, method_tag_1.features), isouter=True)
        option_args.append(orm.contains_eager(
            method_tag_1.features.of_type(feature_1)))

    if option_args:
        return query.options(*option_args)

    return build_method_tag_core_request(selection_set, name)


def request_method_tags(_obj, info, name=None):
    query = build_method_tag_request(_obj, info, name=name)
    return query.distinct().all()
