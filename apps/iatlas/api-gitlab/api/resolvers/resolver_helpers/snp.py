from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Snp
from .general_resolvers import build_join_condition, get_selected, get_value
from .paging_utils import get_cursor, get_pagination_queries, Paging

snp_request_fields = {'id', 'name', 'rsid', 'chr', 'bp'}


def build_snp_graphql_response(snp):
    return {
        'id': get_value(snp, 'id'),
        'name': get_value(snp, 'snp_name') or get_value(snp),
        'rsid': get_value(snp, 'snp_rsid') or get_value(snp, 'rsid'),
        'chr': get_value(snp, 'snp_chr') or get_value(snp, 'chr'),
        'bp': get_value(snp, 'snp_bp') or get_value(snp, 'bp')
    }


def build_snp_request(
        requested, name=None, rsid=None, chr=None, max_bp=None, min_bp=None, distinct=False, paging=None):
    """
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request

    All keyword arguments are optional. Keyword arguments are:
        `name` - a list of strings
        `rsid` - a list of strings
        `chr` - a list of strings
        `max_bp` - an integer
        `min_bp` - an integer
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `paging` - a dict containing pagination metadata
    """
    sess = db.session

    snp_1 = aliased(Snp, name='snp')

    core_field_mapping = {
        'id': snp_1.id.label('id'),
        'name': snp_1.name.label('name'),
        'rsid': snp_1.rsid.label('rsid'),
        'chr': snp_1.chr.label('chr'),
        'bp': snp_1.bp.label('bp')
    }

    core = get_selected(requested, core_field_mapping)

    if distinct == False:
        # Add the id as a cursor if not selecting distinct
        core |= {snp_1.id.label('id')}

    query = sess.query(*core)
    query = query.select_from(snp_1)

    if name:
        query = query.filter(snp_1.name.in_(name))

    if rsid:
        query = query.filter(snp_1.rsid.in_(rsid))

    if chr:
        query = query.filter(snp_1.chr.in_(chr))

    if max_bp or max_bp == 0:
        query = query.filter(snp_1.bp <= max_bp)

    if min_bp or min_bp == 0:
        query = query.filter(snp_1.bp >= min_bp)

    return get_pagination_queries(query, paging, distinct, cursor_field=snp_1.id)
