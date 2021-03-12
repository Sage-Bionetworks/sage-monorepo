from sqlalchemy import and_
from sqlalchemy.orm import aliased, contains_eager
from api import db
from api.db_models import Dataset, Sample
from .general_resolvers import get_selected, get_value
from .sample import build_sample_graphql_response

simple_data_set_request_fields = {'display', 'name', 'type'}

data_set_request_fields = simple_data_set_request_fields.union({'samples'})


def build_data_set_graphql_response(data_set, prefix='data_set_'):
    return {
        'display': get_value(data_set, prefix + 'display') or get_value(data_set, 'display'),
        'name': get_value(data_set, prefix + 'name') or get_value(data_set),
        'samples': map(build_sample_graphql_response, get_value(data_set, 'samples', [])),
        'type': get_value(data_set, prefix + 'type') or get_value(data_set, 'type'),
    }


def build_data_set_request(requested, data_set=None, sample=None, data_set_type=None):
    '''
    Builds a SQL query.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `sample` - a list of strings, sample names
        `data_set_type` - a list of strings, data set types
    '''
    sess = db.session

    data_set_1 = aliased(Dataset, name='d')
    sample_1 = aliased(Sample, name='s')

    core_field_mapping = {'display': data_set_1.display.label('display'),
                          'name': data_set_1.name.label('name'),
                          'type': data_set_1.data_set_type.label('type')}
    sample_core_field_mapping = {'name': sample_1.name.label('name')}

    core = get_selected(requested, core_field_mapping)

    option_args = []

    query = sess.query(data_set_1)

    if 'samples' in requested or sample:
        query = query.join((sample_1, data_set_1.samples), isouter=True)
        option_args.append(contains_eager(
            data_set_1.samples.of_type(sample_1)))

    if option_args:
        query = query.options(*option_args)
    else:
        query = sess.query(*core)

    if data_set:
        query = query.filter(data_set_1.name.in_(data_set))

    if data_set_type:
        query = query.filter(data_set_1.data_set_type.in_(data_set_type))

    if sample:
        query = query.filter(sample_1.name.in_(sample))

    return query


def request_data_sets(*args, **kwargs):
    '''
    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `sample` - a list of strings, sample names
        `data_set_type` - a list of strings, data set types
    '''
    query = build_data_set_request(*args, **kwargs)
    return query.distinct().all()
