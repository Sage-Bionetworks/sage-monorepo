from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, CellStat, Gene
from .general_resolvers import build_join_condition, get_selected, get_value
from .data_set import build_data_set_graphql_response
from .gene import build_gene_graphql_response
from .paging_utils import get_pagination_queries

cell_stat_request_fields = {
    "id",
    "dataSet",
    "gene",
    "snp",
    "type",
    "count",
    "avgExpr",
    "percExpr",
}


def build_cell_stat_graphql_response(cell_stat):
    return {
        "id": get_value(cell_stat, "id"),
        "dataSet": build_data_set_graphql_response()(cell_stat),
        "gene": build_gene_graphql_response()(cell_stat),
        "type": get_value(cell_stat, "type"),
        "count": get_value(cell_stat, "count"),
        "avgExpr": get_value(cell_stat, "avg_expr"),
        "percExpr": get_value(cell_stat, "perc_expr"),
    }


def build_cell_stat_request(
    requested,
    data_set_requested,
    gene_requested,
    distinct=False,
    paging=None,
    entrez=None,
):
    """
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'dataSet' node of the graphql request. If 'dataSet' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'gene' node of the graphql request. If 'gene' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `paging` - a dict containing pagination metadata
        `entrez` - a list of ints, entrez ids
    """
    sess = db.session

    cell_stat_1 = aliased(CellStat, name="cell_stat")
    data_set_1 = aliased(Dataset, name="ds")
    gene_1 = aliased(Gene, name="g")

    core_field_mapping = {
        "id": cell_stat_1.id.label("id"),
        "type": cell_stat_1.cell_type.label("type"),
        "count": cell_stat_1.cell_count.label("count"),
        "avgExpr": cell_stat_1.avg_expr.label("avg_expr"),
        "percExpr": cell_stat_1.perc_expr.label("perc_expr"),
    }
    data_set_core_field_mapping = {
        "display": data_set_1.display.label("data_set_display"),
        "name": data_set_1.name.label("data_set_name"),
        "type": data_set_1.dataset_type.label("data_set_type"),
    }
    gene_core_field_mapping = {
        "entrez": gene_1.entrez_id.label("gene_entrez"),
        "hgnc": gene_1.hgnc_id.label("gene_hgnc"),
    }

    core = get_selected(requested, core_field_mapping)
    core |= get_selected(data_set_requested, data_set_core_field_mapping)
    core |= get_selected(gene_requested, gene_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(cell_stat_1)

    if "dataSet" in requested:
        data_set_join_condition = build_join_condition(
            data_set_1.id, cell_stat_1.dataset_id
        )
        query = query.join(data_set_1, and_(*data_set_join_condition), isouter=False)

    if "gene" in requested or entrez:
        is_outer = not bool(entrez)
        gene_join_condition = build_join_condition(
            gene_1.id,
            cell_stat_1.gene_id,
            filter_column=gene_1.entrez_id,
            filter_list=entrez,
        )
        query = query.join(gene_1, and_(*gene_join_condition), isouter=is_outer)

    return get_pagination_queries(query, paging, distinct, cursor_field=cell_stat_1.id)
