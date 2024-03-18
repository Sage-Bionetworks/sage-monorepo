from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Cell, CellToFeature, CellToGene, CellToSample, Sample, Feature, Gene, CohortToSample, Cohort
from .general_resolvers import build_join_condition, get_selected, get_value
from .feature import build_feature_graphql_response
from .gene import build_gene_graphql_response
from .paging_utils import get_pagination_queries

cell_request_fields = {
    'id',
    'type',
    'name',
    'features',
    'genes',
}


def build_cell_graphql_response(
    requested=[],
    gene_requested=[],
    feature_requested=[],
    entrez=None,
    feature=None,
    prefix='cell_'
):

    def f(cell):
        if not cell:
            return None

        id = get_value(cell, prefix + 'id')
        genes = get_genes(id, requested, gene_requested, entrez)
        features = get_features(id, requested, feature_requested, feature)
        result = {
            'id': id,
            'type': get_value(cell, prefix + 'type'),
            'name': get_value(cell, prefix + 'name'),
            'genes': map(build_gene_graphql_response(), genes),
            'features': map(build_feature_graphql_response(), features)
        }
        return result
    return f


def build_cell_request(
        requested,
        feature_requested,
        gene_requested,
        distinct=False,
        paging=None,
        entrez=None,
        feature=None,
        cohort=None,
        cell=None
):
    sess = db.session

    cell_1 = aliased(Cell, name='cell')
    cell_to_feature_1 = aliased(CellToFeature, name='ctf')
    cell_to_gene_1 = aliased(CellToGene, name='ctg')
    cell_to_sample_1 = aliased(CellToSample, name='cts')
    feature_1 = aliased(Feature, name='f')
    gene_1 = aliased(Gene, name='g')
    sample_1 = aliased(Sample, name = 's')
    cohort_to_sample_1 = aliased(CohortToSample, name = 'cots')
    cohort_1 = aliased(Cohort, name = 'c')


    core_field_mapping = {
        'id': cell_1.id.label('cell_id'),
        'type': cell_1.cell_type.label('cell_type'),
        'name': cell_1.name.label('cell_name'),
    }


    core = get_selected(requested, core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(cell_1)

    if cell:
        query = query.filter(cell_1.name.in_(cell))

    if feature:

        feature_subquery = sess.query(cell_to_feature_1.cell_id)

        feature_join_condition = build_join_condition(
            cell_to_feature_1.feature_id,
            feature_1.id,
            filter_column=feature_1.name,
            filter_list=feature
        )
        feature_subquery = feature_subquery.join(
            feature_1,
            and_(*feature_join_condition),
            isouter=False
        )

        query = query.filter(cell_1.id.in_(feature_subquery))

    if entrez:

        gene_subquery = sess.query(cell_to_gene_1.cell_id)

        gene_join_condition = build_join_condition(
            cell_to_gene_1.gene_id,
            gene_1.id,
            filter_column=gene_1.entrez_id,
            filter_list=entrez
        )
        gene_subquery = gene_subquery.join(
            gene_1,
            and_(*gene_join_condition),
            isouter=False
        )

        query = query.filter(cell_1.id.in_(gene_subquery))

    if cohort:

        cohort_subquery = sess.query(cell_to_sample_1.cell_id)

        sample_join_condition = build_join_condition(
            cell_to_sample_1.sample_id,
            sample_1.id
        )
        cohort_subquery = cohort_subquery.join(
            sample_1,
            and_(*sample_join_condition),
            isouter=False
        )

        cohort_to_sample_join_condition = build_join_condition(
            sample_1.id,
            cohort_to_sample_1.sample_id,
        )
        cohort_subquery = cohort_subquery.join(
            cohort_to_sample_1,
            and_(*cohort_to_sample_join_condition),
            isouter=False
        )

        cohort_join_condition = build_join_condition(
            cohort_to_sample_1.cohort_id,
            cohort_1.id,
            filter_column=cohort_1.name,
            filter_list=cohort
        )
        cohort_subquery = cohort_subquery.join(
            cohort_1,
            and_(*cohort_join_condition),
            isouter=False
        )

        query = query.filter(cell_1.id.in_(cohort_subquery))

    import logging
    logging.warning(query)

    return get_pagination_queries(query, paging, distinct, cursor_field=cell_1.id)



def get_genes(cell_id, requested, gene_requested, entrez=None):

    if 'genes' not in requested:
        return []

    sess = db.session

    cell_to_gene_1 = aliased(CellToGene, name='ctg')
    gene_1 = aliased(Gene, name = 'g')

    core_field_mapping = {
        'id': gene_1.id.label('gene_id'),
        'entrez': gene_1.entrez_id.label('gene_entrez_id'),
        'hgnc': gene_1.hgnc_id.label('gene_hgnc_id'),
        'singleCellSeq': cell_to_gene_1.single_cell_seq.label('gene_single_cell_seq')
    }

    core = get_selected(gene_requested, core_field_mapping)
    query = sess.query(*core)
    query = query.select_from(cell_to_gene_1)
    query = query.filter(cell_to_gene_1.cell_id.in_([cell_id]))

    gene_join_condition = build_join_condition(
        cell_to_gene_1.gene_id,
        gene_1.id,
        filter_column=gene_1.entrez_id,
        filter_list=entrez
    )
    query = query.join(
        gene_1,
        and_(*gene_join_condition),
        isouter=False
    )

    genes = query.distinct().all()
    return genes


def get_features(cell_id, requested, feature_requested, feature):

    if 'features' not in requested:
        return []

    sess = db.session

    cell_to_feature_1 = aliased(CellToFeature, name='ctf')
    feature_1 = aliased(Feature, name = 'g')

    core_field_mapping = {
        'id': feature_1.id.label('fetaure_id'),
        'name': feature_1.name.label('feature_name'),
        'display': feature_1.display.label('feature_display'),
        'value': cell_to_feature_1.feature_value.label('feature_value')
    }

    core = get_selected(feature_requested, core_field_mapping)
    query = sess.query(*core)
    query = query.select_from(cell_to_feature_1)
    query = query.filter(cell_to_feature_1.cell_id.in_([cell_id]))

    feature_join_condition = build_join_condition(
        cell_to_feature_1.feature_id,
        feature_1.id,
        filter_column=feature_1.name,
        filter_list=feature
    )
    query = query.join(
        feature_1,
        and_(*feature_join_condition),
        isouter=False
    )

    features = query.distinct().all()
    return features