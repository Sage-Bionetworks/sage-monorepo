from .resolver_helpers import get_value, request_related


def resolve_related(_obj, info, dataSet=None, related=None):
    results = request_related(
        _obj, info=info, data_set=dataSet, related=related)

    data_set_dict = dict()
    for row in results:
        data_set = get_value(row, 'data_set')
        try:
            data_set_dict[data_set].append(row)
        except KeyError:
            data_set_dict[data_set] = [row]

    return [{
        'display': get_value(value[0], 'data_set_display'),
        'dataSet': key,
        'related': [{
            'characteristics': get_value(row, 'characteristics'),
            'color': get_value(row, 'color'),
            'display': get_value(row, 'display'),
            'name': get_value(row, 'name')
        } for row in value]
    } for key, value in data_set_dict.items()]
