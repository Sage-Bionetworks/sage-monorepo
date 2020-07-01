from .resolver_helpers import request_tags, get_value


def resolve_tags(_obj, info, dataSet, related, feature=None, featureClass=None):
    results = request_tags(_obj, info=info, data_set=dataSet, related=related,
                           feature=feature, feature_class=featureClass)

    return [{
        "characteristics": get_value(row, 'characteristics'),
        "color": get_value(row, 'color'),
        "display": get_value(row, 'display'),
        "name": get_value(row, 'name'),
        "sampleCount": get_value(row, 'sample_count'),
        "samples": get_value(row, 'samples'),
    } for row in results]
