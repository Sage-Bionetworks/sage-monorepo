from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Cohort, Dataset, Tag
from .general_resolvers import build_join_condition, get_selected, get_value
from .paging_utils import get_cursor, get_pagination_queries, Paging

cohort_request_fields = {'id', 'name', 'dataSet', 'tag', 'clinical'}


def build_cohort_graphql_response(cohort):
    return {
        'id': get_value(cohort, 'id'),
        'name': get_value(cohort, 'cohort_name') or get_value(cohort),
        'dataSet': get_value(cohort, 'cohort_dataSet') or get_value(cohort, 'data_set'),
        'tag': get_value(cohort, 'cohort_tag') or get_value(cohort, 'tag'),
        'clinical': get_value(cohort, 'cohort_clinical') or get_value(cohort, 'clinical')
    }


def build_cohort_request(requested, data_set_requested, tag_requested, name=None, data_set=None, tag=None, clinical=None, distinct=False, paging=None):
    """
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'dataSet' node of the graphql request. If 'dataSet' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'tag' node of the graphql request. If 'tag' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `name` - a list of strings
        `data_set` - a list of strings, data set names
        `tag` - a list of strings, tag names
        `clinical` - a list of strings, clincial variable names
    """
    sess = db.session

    cohort_1 = aliased(Cohort, name='crt')
    data_set_1 = aliased(Dataset, name='ds')
    tag_1 = aliased(Tag, name='t')

    core_field_mapping = {
        'id': cohort_1.id.label('id'),
        'name': cohort_1.name.label('name'),
        'clinical': cohort_1.clinical.label('clinical')
    }

    data_set_core_field_mapping = {'display': data_set_1.display.label('data_set_display'),
                                   'name': data_set_1.name.label('data_set_name'),
                                   'type': data_set_1.data_set_type.label('data_set_type')}
    tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                              'color': tag_1.color.label('color'),
                              'longDisplay': tag_1.long_display.label('tag_long_display'),
                              'name': tag_1.name.label('tag_name'),
                              'shortDisplay': tag_1.short_display.label('tag_short_display')}

    core = get_selected(requested, core_field_mapping)
    core |= get_selected(data_set_requested, data_set_core_field_mapping)
    core |= get_selected(tag_requested, tag_core_field_mapping)

    if distinct == False:
        # Add the id as a cursor if not selecting distinct
        core |= {cohort_1.id.label('id')}

    query = sess.query(*core)
    query = query.select_from(cohort_1)

    if name:
        query = query.filter(cohort_1.name.in_(name))

    if clinical:
        query = query.filter(cohort_1.clinical.in_(clinical))

    if 'dataSet' in requested or data_set:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            data_set_1.id, cohort_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'tag' in requested or tag:
        is_outer = not bool(tag)
        data_set_join_condition = build_join_condition(
            tag_1.id, cohort_1.tag_id, filter_column=tag_1.name, filter_list=tag)
        query = query.join(tag_1, and_(
            *data_set_join_condition), isouter=is_outer)

    return get_pagination_queries(query, paging, distinct, cursor_field=cohort_1.id)
