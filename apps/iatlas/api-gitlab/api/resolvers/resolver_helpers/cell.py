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
}

feature_related_cell_request_fields = {
    'name',
    'type',
    'value'
}

gene_related_cell_request_fields = {
    'name',
    'type',
    'singleCellSeq'
}

def build_cell_graphql_response(
    requested=[],
    prefix='cell_'
):

    def f(cell):
        if not cell:
            return None

        id = get_value(cell, prefix + 'id')
        result = {
            'id': id,
            'type': get_value(cell, prefix + 'type'),
            'name': get_value(cell, prefix + 'name'),
            'value': get_value(cell, prefix + 'feature_value'),
            'singleCellSeq': get_value(cell, prefix + 'single_cell_seq'),
        }
        return result
    return f


def build_cell_request(
        requested,
        distinct=False,
        paging=None,
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

    return get_pagination_queries(query, paging, distinct, cursor_field=cell_1.id)
