from .resolver_helpers import get_value, request_tags


def resolve_tags(_obj, info, dataSet, related, tag=None, feature=None, featureClass=None):
    results = request_tags(_obj, info=info, data_set=dataSet, related=related,
                           tag=tag, feature=feature, feature_class=featureClass,
                           get_samples=False)

    return [{
        "characteristics": get_value(row, 'characteristics'),
        "color": get_value(row, 'color'),
        "display": get_value(row, 'display'),
        "name": get_value(row, 'name'),
        "sampleCount": get_value(row, 'sample_count'),
        "samples": get_value(row, 'samples'),
    } for row in results]
