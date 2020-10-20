from sqlalchemy import and_, orm
from api import db
from api.db_models import Gene, GeneType
from .general_resolvers import build_option_args, get_selection_set

simple_gene_type_request_fields = {'display', 'name'}

gene_type_request_fields = simple_gene_type_request_fields.union({'genes'})


def build_gene_type_core_request(selection_set, name=None):
    """
    Builds a SQL request with just core gene type fields.
    """
    sess = db.session

    gene_type_1 = orm.aliased(GeneType, name='g')

    core_field_mapping = {'display': gene_type_1.display.label('display'),
                          'name': gene_type_1.name.label('name')}

    core = build_option_args(selection_set, core_field_mapping)

    requested = build_option_args(
        selection_set, {'display': 'display', 'name': 'name'})

    query = sess.query(*core)

    if name:
        query = query.filter(gene_type_1.name.in_(name))

    order = []
    append_to_order = order.append
    if 'name' in requested:
        append_to_order(gene_type_1.name)
    if 'display' in requested:
        append_to_order(gene_type_1.display)
    query = query.order_by(*order) if order else query

    return query


def build_gene_type_request(_obj, info, name=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(info=info)

    gene_1 = orm.aliased(Gene, name='g')
    gene_type_1 = orm.aliased(GeneType, name='m')

    related_field_mapping = {'genes': 'genes'}

    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []

    query = sess.query(gene_type_1)

    if name:
        query = query.filter(gene_type_1.name.in_(name))

    if 'genes' in relations:
        query = query.join((gene_1, gene_type_1.genes), isouter=True)
        option_args.append(orm.contains_eager(
            gene_type_1.genes.of_type(gene_1)))

    query = query.order_by(gene_type_1.name, gene_type_1.display)

    if option_args:
        return query.options(*option_args)

    return build_gene_type_core_request(selection_set, name)


def request_gene_types(_obj, info, name=None):
    query = build_gene_type_request(_obj, info, name=name)
    return query.distinct().all()
