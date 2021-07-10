from sqlalchemy.orm import aliased
from api import db
from sqlalchemy import and_
from api.db_models import Dataset, Sample, DatasetToSample, DatasetToTag, Tag
from .general_resolvers import get_selected, get_value, build_join_condition
from .sample import build_sample_graphql_response
from .tag import build_tag_graphql_response
from .paging_utils import get_pagination_queries


simple_data_set_request_fields = {'display', 'name', 'type'}

data_set_request_fields = simple_data_set_request_fields.union({
                                                               'samples', 'tags'})


def build_data_set_graphql_response(prefix='data_set_', requested=[], sample_requested=[], tag_requested=[], sample=None):

    def f(data_set):
        if not data_set:
            return None
        else:
            id = get_value(data_set, prefix + 'id')
            samples = get_samples(id, requested, sample_requested, sample)
            tags = get_tags(id, requested, tag_requested)
            dict = {
                'id': id,
                'display': get_value(data_set, prefix + 'display'),
                'name': get_value(data_set, prefix + 'name'),
                'type': get_value(data_set, prefix + 'type'),
                'samples': map(build_sample_graphql_response(), samples),
                'tags': map(build_tag_graphql_response(), tags),
            }
            return(dict)
    return(f)


def build_data_set_request(requested, data_set=None, sample=None, data_set_type=None, distinct=False, paging=None):
    '''
    Builds a SQL query.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `sample` - a list of strings, sample names
        `data_set_type` - a list of strings, data set types
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `paging` - a dict containing pagination metadata
    '''
    sess = db.session

    data_set_1 = aliased(Dataset, name='d')
    data_set_to_sample_1 = aliased(DatasetToSample, name='dts')
    sample_1 = aliased(Sample, name='s')

    core_field_mapping = {
        'display': data_set_1.display.label('data_set_display'),
        'name': data_set_1.name.label('data_set_name'),
        'type': data_set_1.data_set_type.label('data_set_type')
    }

    core = get_selected(requested, core_field_mapping)
    core |= {data_set_1.id.label('data_set_id')}

    query = sess.query(*core)
    query = query.select_from(data_set_1)

    if sample:

        data_set_to_sample_subquery = sess.query(
            data_set_to_sample_1.dataset_id)

        sample_join_condition = build_join_condition(
            data_set_to_sample_1.sample_id, sample_1.id, filter_column=sample_1.name, filter_list=sample)

        data_set_to_sample_subquery = data_set_to_sample_subquery.join(sample_1, and_(
            *sample_join_condition), isouter=False)

        query = query.filter(data_set_1.id.in_(data_set_to_sample_subquery))

    if data_set:
        query = query.filter(data_set_1.name.in_(data_set))

    if data_set_type:
        query = query.filter(data_set_1.data_set_type.in_(data_set_type))

    return get_pagination_queries(query, paging, distinct, cursor_field=data_set_1.id)


def get_samples(dataset_id, requested, sample_requested, sample=None):
    if 'samples' in requested:
        sess = db.session

        data_set_to_sample_1 = aliased(DatasetToSample, name='dts')
        sample_1 = aliased(Sample, name='s')

        sample_core_field_mapping = {
            'name': sample_1.name.label('sample_name')}

        sample_core = get_selected(sample_requested, sample_core_field_mapping)
        #sample_core |= {sample_1.id.label('sample_id')}

        sample_query = sess.query(*sample_core)
        sample_query = sample_query.select_from(sample_1)

        if sample:
            sample_query = sample_query.filter(sample_1.name.in_(sample))

        data_set_to_sample_subquery = sess.query(
            data_set_to_sample_1.sample_id)

        data_set_to_sample_subquery = data_set_to_sample_subquery.filter(
            data_set_to_sample_1.dataset_id == dataset_id)

        sample_query = sample_query.filter(
            sample_1.id.in_(data_set_to_sample_subquery))

        return sample_query.distinct().all()

    return []


def get_tags(dataset_id, requested, tag_requested):
    if 'tags' in requested:
        sess = db.session

        data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
        tag_1 = aliased(Tag, name='t')

        core_field_mapping = {
            'characteristics': tag_1.characteristics.label('tag_characteristics'),
            'color': tag_1.color.label('tag_color'),
            'longDisplay': tag_1.long_display.label('tag_long_display'),
            'name': tag_1.name.label('tag_name'),
            'shortDisplay': tag_1.short_display.label('tag_short_display')
        }

        core = get_selected(tag_requested, core_field_mapping)
        query = sess.query(*core)
        query = query.select_from(tag_1)

        subquery = sess.query(data_set_to_tag_1.tag_id)

        subquery = subquery.filter(data_set_to_tag_1.dataset_id == dataset_id)

        query = query.filter(tag_1.id.in_(subquery))

        return query.distinct().all()

    return []
