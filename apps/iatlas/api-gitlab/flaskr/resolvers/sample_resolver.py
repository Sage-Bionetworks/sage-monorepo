from flaskr.db_models import (Sample)
from .resolver_helpers import get_child_value, get_value, build_option_args
from flaskr.database import return_sample_query


valid_sample_node_mapping = {
    'id': 'id',
    'name': 'name',
    'description': 'description',
    'patient': 'patient_id',
    "genes": 'genes'
}

def resolve_sample(_obj, info, id=None, name=None):
    option_args = build_option_args(
        info.field_nodes[0].selection_set,
        valid_sample_node_mapping
    )
    query = return_sample_query(*option_args)
    if id != None:
        sample = query.filter_by(id=id).first()
    elif name != None:
        sample = query.filter_by(name=name).first()
    else:
        return None

    return {
        "id": get_value(sample, 'id'),
        "name": get_value(sample, 'name'),
        "patient": get_value(sample, 'patient'),
        "genes": get_child_value(get_value(sample, 'genes'))
    }

def resolve_samples(_obj, info, id=None, name=None):
    option_args = build_option_args(
        info.field_nodes[0].selection_set,
        valid_sample_node_mapping
    )
    query = return_sample_query(*option_args)
    if id is not None:
        query = query.filter(Sample.id.in_(id))
    elif name is not None:
        query = query.filter(Sample.name.in_(name))
    else:
        return None
    samples = query.all()
    return [{
        "id": get_value(sample, 'id'),
        "name": get_value(sample, 'name'),
        "description": get_value(sample, 'description'),
        "patient": get_value(sample, 'patient')
    } for sample in samples]