from .resolver_helpers import get_value, request_features, return_feature_value


def resolve_features_by_tag(_obj, info, dataSet=None, related=None, feature=None, featureClass=None):
    results = request_features(_obj, info, data_set=dataSet, related=related, feature=feature,
                               feature_class=featureClass, by_class=False, by_tag=True)

    tag_map = dict()
    for row in results:
        feature_tag = get_value(row, 'tag')
        try:
            tag_map[feature_tag].append(row)
        except KeyError:
            tag_map[feature_tag] = [row]

    return [{
        'characteristics': get_value(value[0], 'tag_characteristics'),
        'display': get_value(value[0], 'tag_display'),
        'features': [{
            'class': get_value(row, 'class'),
            'display': get_value(row, 'display'),
            'methodTag': get_value(row, 'method_tag'),
            'name': get_value(row),
            'order': get_value(row, 'order'),
            'sample': get_value(row, 'sample'),
            'unit': get_value(row, 'unit'),
            'value': return_feature_value(row)
        } for row in value],
        'tag': key
    } for key, value in tag_map.items()]
