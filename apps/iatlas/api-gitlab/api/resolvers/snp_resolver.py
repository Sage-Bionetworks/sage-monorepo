from api.db_models import (Snp)
from .resolver_helpers import (
    get_requested, snp_request_fields, build_snp_graphql_response, build_snp_request)

from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_snps(_obj, info, name=None, rsid=None, chr=None, maxBP=None, minBP=None, paging=None, distinct=False):
    requested = get_requested(info, snp_request_fields, "items")

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_snp_request(
        requested, name=name, rsid=rsid, chr=chr, max_bp=maxBP, min_bp=minBP, paging=paging, distinct=distinct)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    return paginate(query, count_query, paging, distinct, build_snp_graphql_response, pagination_requested)
