from itertools import groupby
from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, DatasetToTag, Feature, FeatureClass, Gene, GeneToType, GeneType, Node, NodeToTag, Tag
from .general_resolvers import build_join_condition, get_selected, get_value
from api.database.database_helpers import execute_sql
from .paging_utils import create_temp_table, get_pagination_queries

simple_node_request_fields = {
    'label',
    'name',
    'network',
    'score',
    'x',
    'y'
}

node_request_fields = simple_node_request_fields.union({
    'dataSet',
    'feature',
    'gene',
    'tags',
})


def get_node_column_labels(requested, node, prefix='node_', add_id=False):
    mapping = {
        'label': node.label.label(prefix + 'label'),
        'name': node.name.label(prefix + 'name'),
        'network': node.network.label(prefix + 'network'),
        'score': node.score.label(prefix + 'score'),
        'x': node.x.label(prefix + 'x'),
        'y': node.y.label(prefix + 'y')
    }
    labels = get_selected(requested, mapping)

    if add_id:
        labels |= {node.id.label('id')}

    return(labels)


def build_node_graphql_response(requested=[], tag_requested=[], prefix='node_'):
    from .data_set import build_data_set_graphql_response
    from .feature import build_feature_graphql_response
    from .gene import build_gene_graphql_response
    from .tag import build_tag_graphql_response

    def f(node):
        if not node:
            return None
        else:

            node_id = get_value(node, 'id')
            tags = get_tags(node_id, requested, tag_requested)
            has_feature = get_value(node, 'feature_name') or get_value(
                node, 'feature_display') or get_value(node, 'feature_order') or get_value(node, 'feature_unit')
            has_gene = get_value(node, 'gene_entrez') or get_value(node, 'gene_hgnc') or get_value(
                node, 'gene_description') or get_value(node, 'gene_friendly_name') or get_value(node, 'gene_io_landscape_name')
            dict = {
                'id': node_id,
                'label': get_value(node, prefix + 'label'),
                'name': get_value(node, prefix + 'name'),
                'network': get_value(node, prefix + 'network'),
                'score': get_value(node, prefix + 'score'),
                'x': get_value(node, prefix + 'x'),
                'y': get_value(node, prefix + 'y'),
                'dataSet': build_data_set_graphql_response()(node),
                'feature': build_feature_graphql_response()(node) if has_feature else None,
                'gene': build_gene_graphql_response()(node) if has_gene else None,
                'tags': map(build_tag_graphql_response(), tags),
            }
            return(dict)
    return(f)


def build_node_request(requested, data_set_requested, feature_requested, gene_requested, data_set=None, distinct=False, entrez=None, feature=None, feature_class=None, gene_type=None, max_score=None, min_score=None, network=None, paging=None, related=None, tag=None):
    '''
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'dataSet' node of the graphql request. If 'dataSet' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'feature' node of the graphql request. If 'feature' is not requested, this will be an empty set.
        4th position - a set of the requested fields in the 'gene' node of the graphql request. If 'gene' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `distinct` - a boolean, specifies whether or not duplicates should be filtered out
        `entrez` - a list of integers, gene entrez ids
        `feature` - a list of strings, feature names
        `feature_class` - a list of strings, feature class names
        `gene_type` - a list of strings, gene type names
        `max_score` - a float, a maximum score value
        `min_score` - a float, a minimum score value
        `network` -  a list of strings
        'paging' - an instance of PagingInput
            `type` - a string, the type of pagination to perform. Must be either 'OFFSET' or 'CURSOR'."
            `page` - an integer, when performing OFFSET paging, the page number requested.
            `limit` - an integer, when performing OFFSET paging, the number or records requested.
            `first` - an integer, when performing CURSOR paging, the number of records requested AFTER the CURSOR.
            `last` - an integer, when performing CURSOR paging, the number of records requested BEFORE the CURSOR.
            `before` - an integer, when performing CURSOR paging: the CURSOR to be used in tandem with 'last'
            `after` - an integer, when performing CURSOR paging: the CURSOR to be used in tandem with 'first'
        `related` - a list of strings, tag names related to data sets
        `tag` - a list of strings, tag names
    '''
    sess = db.session

    data_set_1 = aliased(Dataset, name='d')
    feature_1 = aliased(Feature, name='f')
    gene_1 = aliased(Gene, name='g')
    node_1 = aliased(Node, name='n')

    data_set_field_mapping = {
        'display': data_set_1.display.label('data_set_display'),
        'name': data_set_1.name.label('data_set_name'),
        'type': data_set_1.data_set_type.label('data_set_type')
    }

    feature_field_mapping = {
        'display': feature_1.display.label('feature_display'),
        'name': feature_1.name.label('feature_name'),
        'order': feature_1.order.label('feature_order'),
        'unit': feature_1.unit.label('feature_unit')
    }

    gene_field_mapping = {
        'entrez': gene_1.entrez.label('gene_entrez'),
        'hgnc': gene_1.hgnc.label('gene_hgnc'),
        'description': gene_1.description.label('gene_description'),
        'friendlyName': gene_1.friendly_name.label('gene_friendly_name'),
        'ioLandscapeName': gene_1.io_landscape_name.label('gene_io_landscape_name')
    }

    node_core = get_node_column_labels(requested, node_1, add_id=True)
    data_set_core = get_selected(data_set_requested, data_set_field_mapping)
    feature_core = get_selected(feature_requested, feature_field_mapping)
    gene_core = get_selected(gene_requested, gene_field_mapping)

    query = sess.query(
        *[*node_core, *data_set_core, *feature_core, *gene_core])
    query = query.select_from(node_1)

    if max_score:
        query = query.filter(node_1.score <= max_score)

    if min_score:
        query = query.filter(node_1.score >= min_score)

    if network:
        query = query.filter(node_1.network.in_(network))

    if tag:
        node_to_tag_2 = aliased(NodeToTag, name='ntt2')
        tag_1 = aliased(Tag, name="t")

        tag_subquery = sess.query(tag_1.id).filter(
            tag_1.name.in_(tag))

        node_tag_join_condition = build_join_condition(
            node_to_tag_2.node_id, node_1.id, node_to_tag_2.tag_id, tag_subquery)

        query = query.join(node_to_tag_2, and_(*node_tag_join_condition))

    if data_set or related or 'dataSet' in requested:
        data_set_join_condition = build_join_condition(
            data_set_1.id, node_1.dataset_id, data_set_1.name, data_set)
        query = query.join(data_set_1, and_(*data_set_join_condition))

    if related:
        data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
        related_tag_1 = aliased(Tag, name='rt')

        related_tag_sub_query = sess.query(related_tag_1.id).filter(
            related_tag_1.name.in_(related))

        data_set_tag_join_condition = build_join_condition(
            data_set_to_tag_1.dataset_id, data_set_1.id, data_set_to_tag_1.tag_id, related_tag_sub_query)
        query = query.join(
            data_set_to_tag_1, and_(*data_set_tag_join_condition))

    if feature or 'feature' in requested or feature_class:
        is_outer = not bool(feature)
        feature_join_condition = build_join_condition(
            feature_1.id, node_1.feature_id, feature_1.name, feature)
        query = query.join(feature_1, and_(
            *feature_join_condition), isouter=is_outer)

        if feature_class:
            feature_class_1 = aliased(FeatureClass, name='fc')
            feature_class_join_condition = build_join_condition(
                feature_class_1.id, feature_1.class_id, feature_class_1.name, feature_class)
            query = query.join(
                feature_class_1, and_(*feature_class_join_condition))

    if entrez or 'gene' in requested or gene_type:
        is_outer = not bool(entrez)
        gene_join_condition = build_join_condition(
            gene_1.id, node_1.gene_id, gene_1.entrez, entrez)
        query = query.join(gene_1, and_(
            *gene_join_condition), isouter=is_outer)

        if gene_type:
            gene_type_1 = aliased(GeneType, name='gt')
            gene_to_type_1 = aliased(GeneToType, name='ggt')
            query = query.join(gene_to_type_1, and_(
                gene_to_type_1.gene_id == gene_1.id, gene_to_type_1.type_id.in_(sess.query(gene_type_1.id).filter(gene_type_1.name.in_(gene_type)))))

    return get_pagination_queries(query, paging, distinct, cursor_field=node_1.id)


def get_tags(node_id, requested, tag_requested):
    if 'tags' in requested:
        from .tag import get_tag_column_labels

        sess = db.session
        tag_1 = aliased(Tag, name='t')
        node_to_tag_1 = aliased(NodeToTag, name='ntt1')
        core = get_tag_column_labels(tag_requested, tag_1)

        query = sess.query(*core)
        query = query.select_from(tag_1)

        node_to_tag_join_condition = build_join_condition(
            tag_1.id, node_to_tag_1.tag_id, node_to_tag_1.node_id, [node_id])

        query = query.join(node_to_tag_1, and_(*node_to_tag_join_condition))
        tags = query.distinct().all()
        return tags

    else:
        return []
