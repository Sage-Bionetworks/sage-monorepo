from .resolver_helpers import get_value, request_data_sets


def resolve_data_sets(_obj, info, dataSet=None, sample=None):
    data_sets = request_data_sets(_obj, info, data_set=dataSet, sample=sample)

    return [{
        'display': get_value(data_set, 'display'),
        'name': get_value(data_set),
        'samples': [{
            'name': get_value(sample),
            'patient': get_value(sample, 'patient')
        } for sample in get_value(data_set, 'samples', [])]
    } for data_set in data_sets]
