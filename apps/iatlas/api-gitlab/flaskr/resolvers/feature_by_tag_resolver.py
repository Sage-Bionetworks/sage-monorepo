from .resolver_helpers import get_value, request_features, return_feature_value


def resolve_features_by_tag(_obj, info, dataSet=None, related=None, feature=None, feature_class=None):
    results = request_features(
        _obj, info, dataSet, related, feature, feature_class, byClass=False, byTag=True)

    tag_map = dict()
    for row in results:
        feature_tag = get_value(row, 'tag')
        if not feature_tag in tag_map:
            tag_map[feature_tag] = [row]
        else:
            tag_map[feature_tag].append(row)

    return [{
        'characteristics': get_value(value[0], 'tag_characteristics'),
        'display': get_value(value[0], 'tag_display'),
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
        'tag': key
    } for key, value in tag_map.items()]
