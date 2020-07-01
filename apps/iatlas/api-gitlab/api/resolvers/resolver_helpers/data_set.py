from sqlalchemy import and_, orm
from api import db
from api.database import return_gene_query
from api.db_models import Dataset, Sample
from .general_resolvers import build_option_args, get_selection_set
from .tag import request_tags


def build_data_set_request(_obj, info, data_set=None, sample=None):
    """
    Builds a SQL request and returns values from the DB.
    """
    sess = db.session

    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)

    data_set_1 = orm.aliased(Dataset, name='d')
    sample_1 = orm.aliased(Sample, name='s')

    core_field_mapping = {'display': data_set_1.display.label('display'),
                          'name': data_set_1.name.label('name')}

    related_field_mapping = {'samples': 'samples'}

    core = build_option_args(selection_set, core_field_mapping)
    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []

    query = sess.query(data_set_1)

    if 'samples' in relations or sample:
        query = query.join((sample_1, data_set_1.samples), isouter=True)
        option_args.append(orm.contains_eager(
            data_set_1.samples.of_type(sample_1)))

    if option_args:
        query = query.options(*option_args)
    else:
        query = sess.query(*core)

    if sample:
        query = query.filter(sample_1.name.in_(sample))

    if data_set:
        query = query.filter(data_set_1.name.in_(data_set))

    return query


def request_data_set(_obj, info, name=None):
    if name:
        name = [name]
        query = build_data_set_request(_obj, info, name=name)
        return query.one_or_none()
    return None


def request_data_sets(_obj, info, data_set=None, sample=None):
    query = build_data_set_request(
        _obj, info, data_set=data_set, sample=sample)
    query = query.distinct()
    return query.all()
