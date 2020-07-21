from sqlalchemy import and_, orm
from api import db
from api.db_models import Gene, ImmuneCheckpoint
from .general_resolvers import build_option_args, get_selection_set


def build_immune_checkpoint_core_request(selection_set, name=None):
    """
    Builds a SQL request with just core immune checkpoint fields.
    """
    sess = db.session

    immune_checkpoint_1 = orm.aliased(ImmuneCheckpoint, name='p')

    core_field_mapping = {'name': immune_checkpoint_1.name.label('name')}

    core = build_option_args(selection_set, core_field_mapping)

    query = sess.query(*core)

    if name:
        query = query.filter(immune_checkpoint_1.name.in_(name))

    return query


def build_immune_checkpoint_request(_obj, info, name=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)

    gene_1 = orm.aliased(Gene, name='g')
    immune_checkpoint_1 = orm.aliased(ImmuneCheckpoint, name='pw')

    related_field_mapping = {'genes': 'genes'}

    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []

    query = sess.query(immune_checkpoint_1)

    if name:
        query = query.filter(immune_checkpoint_1.name.in_(name))

    if 'genes' in relations:
        query = query.join((gene_1, immune_checkpoint_1.genes), isouter=True)
        option_args.append(orm.contains_eager(
            immune_checkpoint_1.genes.of_type(gene_1)))

    if option_args:
        return query.options(*option_args)

    return build_immune_checkpoint_core_request(selection_set, name)


def request_immune_checkpoints(_obj, info, name=None):
    query = build_immune_checkpoint_request(_obj, info, name=name)
    return query.distinct().all()
