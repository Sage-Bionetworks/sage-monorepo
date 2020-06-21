from .resolver_helpers import get_value, request_features, return_feature_value


def resolve_features_by_class(_obj, info, dataSet=None, related=None, feature=None, featureClass=None):
    results = request_features(
        _obj, info, dataSet, related, feature, featureClass, byClass=True, byTag=False)

    class_map = dict()
    for row in results:
        feature_class = get_value(row, 'class')
        if not feature_class in class_map:
            class_map[feature_class] = [row]
        else:
            class_map[feature_class].append(row)

    return [{
        'class': key,
        'features': [{
            'class': get_value(row, 'class'),
            'display': get_value(row, 'display'),
            'methodTag': get_value(row, 'method_tag'),
            'name': get_value(row, 'name'),
            'order': get_value(row, 'order'),
            'sample': get_value(row, 'sample'),
            'unit': get_value(row, 'unit'),
            'value': return_feature_value(row)
        } for row in value],
    } for key, value in class_map.items()]
